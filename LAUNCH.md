# Launch checklist

Current release: **v1.0.0**

## Before announcing

- [ ] Commit and push latest `main` to GitHub
- [ ] Tag `v1.0.0` on that commit and push the tag
- [ ] Publish the GitHub Release **NODO v1.0.0** with:
  - `nodo-1.0.0.jar` (from `./gradlew exportReleaseJar`)
  - `samples/TestOpmode.blk`
- [ ] Trigger [JitPack](https://jitpack.io/#scarlettychen/NODO) for tag `v1.0.0` (Look up → Get it → wait for green)
- [ ] Confirm docs site: https://scarlettychen.github.io/NODO/
- [ ] Test Java: `implementation 'com.github.scarlettychen:NODO:v1.0.0'` in a TeamCode project
- [ ] Test OnBot: upload `nodo-1.0.0.jar`, Build Everything, confirm **NODO Init** / **NODO Run**
- [ ] Import `TestOpmode.blk` and run drive + turn on a robot

## Build artifacts locally

```bash
./gradlew assembleRelease exportReleaseJar
```

| Output | Path |
|--------|------|
| AAR (Android Studio) | `build/outputs/aar/nodo-release.aar` |
| JAR (OnBot / Blocks) | `build/outputs/jar/nodo-1.0.0.jar` |
| Blocks sample | `samples/TestOpmode.blk` |

## Team install (copy-paste)

**Gradle** — root `build.gradle` needs JitPack; **TeamCode** `build.gradle`:

```gradle
implementation 'com.github.scarlettychen:NODO:v1.0.0'
```

**OnBot / Blocks** — [Releases](https://github.com/scarlettychen/NODO/releases/latest) → download JAR → Upload → Build Everything.

## Docs

Published from `/docs` on `main`. After doc changes, push and hard-refresh the site in ~1–3 minutes.

## Version bump (next release)

1. Update `version` in `build.gradle`
2. Update `docs/installation.md` and `LAUNCH.md` coordinates
3. `./gradlew exportReleaseJar`
4. `git tag -a vX.Y.Z -m "..."` → push tag → GitHub Release → attach JAR + `.blk`
