#!/usr/bin/env python3
"""Fail when a localized resource exists in the base locale but not in every translation.

moko-resources silently falls back to the base value for a missing key, so an untranslated
string reads as English to a German or French user and nothing in the build notices. This
compares the key sets and fails the run instead.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

RESOURCES = Path("i18n/generator/src/commonMain/moko-resources")
BASE = "base"
FILES = ("strings.xml", "plurals.xml")
KEY_PATTERN = re.compile(r"<(string|plurals)\s+name=\"([^\"]+)\"")


def keys_in(path: Path) -> list[str]:
    if not path.exists():
        return []
    return [name for _, name in KEY_PATTERN.findall(path.read_text(encoding="utf-8"))]


def locales() -> list[str]:
    return sorted(d.name for d in RESOURCES.iterdir() if d.is_dir() and d.name != BASE)


def main() -> int:
    if not RESOURCES.is_dir():
        print(f"::error::Resource directory not found: {RESOURCES}")
        return 1

    translations = locales()
    if not translations:
        print(f"::error::No translation locales found beside {BASE} in {RESOURCES}")
        return 1

    failed = False

    for filename in FILES:
        base_keys = keys_in(RESOURCES / BASE / filename)
        if not base_keys:
            print(f"::error::{BASE}/{filename} has no keys, or is missing")
            failed = True
            continue

        duplicates = sorted({k for k in base_keys if base_keys.count(k) > 1})
        if duplicates:
            failed = True
            for key in duplicates:
                print(f"::error file={RESOURCES / BASE / filename}::Duplicate key: {key}")

        for locale in translations:
            path = RESOURCES / locale / filename
            found = keys_in(path)

            for key in sorted({k for k in found if found.count(k) > 1}):
                failed = True
                print(f"::error file={path}::Duplicate key: {key}")

            missing = sorted(set(base_keys) - set(found))
            for key in missing:
                failed = True
                print(f"::error file={path}::Missing translation for {key}")

            unknown = sorted(set(found) - set(base_keys))
            for key in unknown:
                failed = True
                print(f"::error file={path}::{key} is not in {BASE}/{filename}")

            status = "ok" if not missing and not unknown else f"{len(missing)} missing, {len(unknown)} unknown"
            print(f"{filename} {locale}: {status}")

    if failed:
        print("::error::Locale parity check failed. Every key in base must exist in every translation.")
        return 1

    print("Locale parity ok")
    return 0


if __name__ == "__main__":
    sys.exit(main())
