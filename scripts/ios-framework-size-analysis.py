#!/usr/bin/env python3
"""Attributes Objective-C bridge bytes in the TvManiac framework to Gradle modules.

Bridge wrapper symbols (_objc2kotlin_*/_kotlin2objc_*) carry the Kotlin
fully qualified name they wrap. Symbol sizes come from nm -n address deltas,
the package to module mapping comes from walking every .kt file (including
KSP generated sources), and the result is bridge bytes for each module.

Usage:
  scripts/ios-framework-size-analysis.py [--csv] [path-to-framework-binary]

Default output is a table for each module, largest first. --csv prints one
"module/<gradle-path>,bytes" line for each module plus "module/unattributed"
so the lines sum to the total bridge bytes.
"""
import re
import subprocess
import sys
from collections import defaultdict
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
DEFAULT_BINARY = REPO / "ios-framework/build/bin/iosArm64/releaseFramework/TvManiac.framework/TvManiac"

FQN_RE = re.compile(r"(?:kfun|kclass|kprop):#?([A-Za-z0-9_.]+)")
SYMBOL_RE = re.compile(r"^([0-9a-f]{8,16}) ([tT]) (.+)$")


def package_to_module() -> dict:
    mapping = {}
    for kt in REPO.rglob("*.kt"):
        rel = str(kt.relative_to(REPO))
        if rel.startswith("ios/") or "/SourcePackages/" in rel or "/.build/" in rel:
            continue
        if "/src/" in rel:
            module = rel.split("/src/")[0]
        elif "/build/generated/" in rel:
            module = rel.split("/build/")[0]
        else:
            continue
        if module.startswith(("build", "gradle")):
            continue
        try:
            with open(kt, "r", encoding="utf-8", errors="ignore") as f:
                for line in f:
                    line = line.strip()
                    if line.startswith("package "):
                        pkg = line[len("package "):].strip().rstrip(";")
                        prev = mapping.get(pkg)
                        if prev is None or len(module) < len(prev):
                            mapping[pkg] = module
                        break
                    if line and not line.startswith(("//", "/*", "*", "@file")):
                        break
        except OSError:
            pass
    return mapping


def main() -> int:
    args = sys.argv[1:]
    csv = "--csv" in args
    positional = [a for a in args if a != "--csv"]
    binary = Path(positional[0]) if positional else DEFAULT_BINARY
    if not binary.is_file():
        print(f"error: {binary} not found", file=sys.stderr)
        return 1

    mapping = package_to_module()
    packages = sorted(mapping, key=len, reverse=True)

    def module_for(fqn):
        for pkg in packages:
            if fqn.startswith(pkg + "."):
                rest = fqn[len(pkg) + 1:]
                if rest and rest[0].isupper():
                    return mapping[pkg]
        return None

    out = subprocess.run(
        ["nm", "-arch", "arm64", "-n", str(binary)],
        capture_output=True, text=True, check=True,
    ).stdout
    symbols = []
    for line in out.splitlines():
        m = SYMBOL_RE.match(line)
        if m:
            symbols.append((int(m.group(1), 16), m.group(3)))

    per_module = defaultdict(int)
    unattributed = 0
    for i, (addr, name) in enumerate(symbols):
        if "objc2kotlin" not in name and "kotlin2objc" not in name:
            continue
        size = (symbols[i + 1][0] - addr) if i + 1 < len(symbols) else 0
        m = FQN_RE.search(name)
        module = module_for(m.group(1)) if m else None
        if module:
            per_module[module] += size
        else:
            unattributed += size

    ranked = sorted(per_module.items(), key=lambda kv: -kv[1])
    if csv:
        for module, size in ranked:
            print(f"module/{module},{size}")
        print(f"module/unattributed,{unattributed}")
    else:
        total = sum(per_module.values()) + unattributed
        print(f"bridge bytes total: {total:,}")
        for module, size in ranked:
            print(f"{size:>10,}  {module}")
        print(f"{unattributed:>10,}  (unattributed)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
