# Launch checklist

Current release: **v1.0.0-beta.1**

## Before announcing

- [ ] Push latest `main` to GitHub
- [ ] Confirm [GitHub Release](https://github.com/scarlettychen/NODO/releases) has `nodo-1.0.0-beta.jar` (or upload fresh build from `./gradlew exportReleaseJar` → `build/outputs/jar/nodo-1.0.0-beta.1.jar`)
- [ ] Trigger [JitPack](https://jitpack.io/#scarlettychen/NODO) build for tag `v1.0.0-beta.1` (Look up → Get it → wait for green)
- [ ] Confirm docs site: https://scarlettychen.github.io/NODO/
- [ ] Test Java: add `implementation 'com.github.scarlettychen:NODO:v1.0.0-beta.1'` in a real TeamCode project and sync
- [ ] Test OnBot: upload JAR, Build Everything, confirm NODO blocks appear
- [ ] Test one sample OpMode on robot (drive + turn)

## Build artifacts locally

```bash
./gradlew assembleRelease exportReleaseJar
```

| Output | Path |
|--------|------|
| AAR (Android Studio) | `build/outputs/aar/nodo-release.aar` |
| JAR (OnBot / Blocks) | `build/outputs/jar/nodo-1.0.0-beta.1.jar` |

## Team install (copy-paste)

**Gradle** — root `build.gradle` needs JitPack; **TeamCode** `build.gradle`:

```gradle
implementation 'com.github.scarlettychen:NODO:v1.0.0-beta.1'
```

**OnBot / Blocks** — [Releases](https://github.com/scarlettychen/NODO/releases/latest) → download JAR → Upload → Build Everything.

## Docs

Published from `/docs` on `main`. After doc changes, push and hard-refresh the site in ~1–3 minutes.

## Version bump (next release)

1. Update `version` in `build.gradle`
2. Update `docs/installation.md` and `README.md` coordinates
3. `./gradlew exportReleaseJar`
4. `git tag -a vX.Y.Z -m "..."` → push tag → GitHub Release → attach JAR
