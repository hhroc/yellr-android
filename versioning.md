## Versioning
The Yellr android app will follow the common Major, Minor, Patch versioning convention
in the form XX.yyy.zzz where XX denotes major verions, yyy denotes minor, and zzz patch.

### Version Bump Criteria
#### Major
In order for the major version to be bumped there must be a significant change to the app.
This includes the first stable release (1.0.0), or subsequent overhauls to the UI or core
application function. (e.g. updating the UI to use material design).  It is expected that
these versions will be release rarely.

#### Minor
Minor version will be bumped periodically when new features are added which do not constitute
a significant change to user expectations.  An example of a minor version bump might be
a new release that allows users to add different media types such as video or audio.  These
version bumps should occur regularly during the continued development of the app.

#### Patch
Patch version is bumped anytime there is a bug fix or hotfix applied to a major or minor
release.  For example if the previous minor version release allowing audio upload also broke
the text upload feature a patch release will be made to fix the bug.  These releases occur on
an 'as-needed' basis to ensure a consistently good experience for all users.

### Android versionCode
versionCode will follow XX.yyy.zzz naming convention
all leading zeros must be dropped
e.g. version 0.1.0 will have versionCode 00.001.000 => 1000
e.g. version 0.3.99 will have versionCode 00.003.099 => 3099
e.g. version 1.0.0 will have versionCode 01.000.000 => 1000000
this system allows for 100 major versions and 1000 minor and patch versions
and ensures there is no collision.