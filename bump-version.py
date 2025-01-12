import argparse
from enum import Enum
import os
import re


class BumpType(Enum):
    MAJOR = "MAJOR"
    MINOR = "MINOR"
    PATCH = "PATCH"


def bump_version(bump_type: str):
    if bump_type == None:
        print("Bump type not found - assuming PATCH")
        bump_type = "PATCH"
    tag_regex = "##.*(\\d+)\\.(\\d+)\\.(\\d+)"
    new_tag: str = ["0", "0", "0"]
    with open("./CHANGELOG.md", "r+t") as changelog_file:        
        file_content = changelog_file.read()
        tags = re.findall(tag_regex, file_content)
        if len(tags) > 0:
            new_tag = list(tags[0])
        if bump_type.upper() == BumpType.MAJOR.value:
            new_tag[0] = str(int(new_tag[0]) + 1)
            new_tag[1] = "0"
            new_tag[2] = "0"
        elif bump_type.upper() == BumpType.MINOR.value:
            new_tag[1] = str(int(new_tag[1]) + 1)
            new_tag[2] = "0"
        elif bump_type.upper() == BumpType.PATCH.value:
            new_tag[2] = str(int(new_tag[2]) + 1)
        else:
            print("Invalid Bump Type")
            return
        new_tag = ".".join(new_tag)
        file_content = file_content.replace(
            "## [Unreleased]", "## [Unreleased]\n\n---\n\n## [" + new_tag + "]"
        )
        changelog_file.seek(0)
        changelog_file.truncate()
        changelog_file.write(file_content)
    print(f"Updated Version: {new_tag}")


def parse_args():
    parser = argparse.ArgumentParser(
        prog="Bump version in CHANGELOG",
        description="""This is a python script that updates the CHANGELOG file by bumping the version for next release""",
    )
    parser.add_argument(
        "-b",
        "--bump-type",
        required=False,
        help="Bump type - MAJOR, MINOR or PATCH",
        type=str,
    )
    return parser.parse_args()


def main():
    args = parse_args()
    bump_version(args.bump_type)


if __name__ == "__main__":
    main()
