#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import math
import statistics
import subprocess
import sys
from datetime import datetime

CONCLUSIONS = ("success", "failure")
BUCKETS = ((1, "<1m"), (5, "1-5m"), (10, "5-10m"), (15, "10-15m"), (20, "15-20m"), (math.inf, ">20m"))


def gh(path: str) -> dict:
    result = subprocess.run(["gh", "api", path], capture_output=True, text=True, check=True)
    return json.loads(result.stdout)


def parse(value: str) -> datetime:
    return datetime.fromisoformat(value.replace("Z", "+00:00"))


def minutes(start: str, end: str) -> float:
    return (parse(end) - parse(start)).total_seconds() / 60


def percentile(values: list[float], fraction: float) -> float:
    ordered = sorted(values)
    return ordered[max(0, math.ceil(fraction * len(ordered)) - 1)]


def completed_runs(workflow: str, event: str, count: int) -> list[dict]:
    runs: list[dict] = []
    page = 1
    while len(runs) < count:
        batch = gh(
            f"repos/{{owner}}/{{repo}}/actions/workflows/{workflow}/runs"
            f"?event={event}&status=completed&per_page=100&page={page}"
        )["workflow_runs"]
        if not batch:
            break
        runs.extend(run for run in batch if run["conclusion"] in CONCLUSIONS)
        page += 1
    return runs[:count]


def finished_jobs(run_id: int) -> list[dict]:
    jobs = gh(f"repos/{{owner}}/{{repo}}/actions/runs/{run_id}/jobs?per_page=100")["jobs"]
    return [
        job
        for job in jobs
        if job["conclusion"] in CONCLUSIONS and job["started_at"] and job["completed_at"]
    ]


def job_report(runs: list[dict], jobs_by_run: dict[int, list[dict]]) -> None:
    durations: dict[str, list[float]] = {}
    queues: dict[str, list[float]] = {}
    finishes: dict[str, list[float]] = {}
    walls: list[float] = []
    job_minutes: list[float] = []
    for run in runs:
        jobs = jobs_by_run[run["id"]]
        if not jobs:
            continue
        start = run["run_started_at"]
        walls.append(max(minutes(start, job["completed_at"]) for job in jobs))
        job_minutes.append(sum(minutes(job["started_at"], job["completed_at"]) for job in jobs))
        for job in jobs:
            name = job["name"]
            durations.setdefault(name, []).append(minutes(job["started_at"], job["completed_at"]))
            queues.setdefault(name, []).append(minutes(job["created_at"], job["started_at"]))
            finishes.setdefault(name, []).append(minutes(start, job["completed_at"]))

    print(f"{'job':<28} {'n':>3} {'dur med':>8} {'dur p90':>8} {'queue med':>10} {'queue p90':>10} {'finish med':>11}")
    for name in sorted(finishes, key=lambda job_name: statistics.median(finishes[job_name]), reverse=True):
        print(
            f"{name[:28]:<28} {len(durations[name]):>3} "
            f"{statistics.median(durations[name]):>8.1f} {percentile(durations[name], 0.9):>8.1f} "
            f"{statistics.median(queues[name]):>10.1f} {percentile(queues[name], 0.9):>10.1f} "
            f"{statistics.median(finishes[name]):>11.1f}"
        )
    print()
    print(
        f"wall clock over {len(walls)} runs: median {statistics.median(walls):.1f}m, "
        f"p90 {percentile(walls, 0.9):.1f}m, max {max(walls):.1f}m"
    )
    print(f"job minutes per run: median {statistics.median(job_minutes):.1f}m")


def step_report(step: str, jobs_by_run: dict[int, list[dict]]) -> None:
    durations = [
        minutes(item["started_at"], item["completed_at"])
        for jobs in jobs_by_run.values()
        for job in jobs
        for item in job.get("steps", [])
        if item["name"] == step and item.get("started_at") and item.get("completed_at")
    ]
    if not durations:
        print(f"No completed step named {step!r}")
        return
    print(
        f"{step}: n {len(durations)}, median {statistics.median(durations):.1f}m, "
        f"p90 {percentile(durations, 0.9):.1f}m, max {max(durations):.1f}m"
    )
    lower = 0.0
    for upper, label in BUCKETS:
        count = sum(1 for value in durations if lower <= value < upper)
        print(f"  {label:>7} {count:>3}")
        lower = upper


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--workflow", default="ci.yml")
    parser.add_argument("--event", default="pull_request")
    parser.add_argument("--runs", type=int, default=40)
    parser.add_argument("--step")
    args = parser.parse_args()

    runs = completed_runs(args.workflow, args.event, args.runs)
    if not runs:
        print(f"No completed {args.event} runs for {args.workflow}")
        return 1

    jobs_by_run = {run["id"]: finished_jobs(run["id"]) for run in runs}
    if args.step:
        step_report(args.step, jobs_by_run)
    else:
        job_report(runs, jobs_by_run)
    return 0


if __name__ == "__main__":
    sys.exit(main())
