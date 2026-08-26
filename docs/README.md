# docs/

Jekyll + [Just the Docs](https://just-the-docs.github.io/just-the-docs/) site for GitHub Pages.

## Site pages

| File | Nav title | Order |
|------|-----------|-------|
| `index.md` | Home | 1 |
| `installation.md` | Installation | 2 |
| `quickstart.md` | Quickstart | 3 |
| `tuning.md` | Tuning Guide | 4 |
| `building.md` | Building Your First Autonomous | 5 |
| `api.md` | API Reference | 6 |
| `privacy.md` | Telemetry & Privacy | 7 (hidden from nav) |
| `language-toggle.md` | Authoring notes | hidden |

## Publish to GitHub Pages

### One-time setup (GitHub website)

1. Push the `docs/` folder to **`main`** on [github.com/scarlettychen/NODO](https://github.com/scarlettychen/NODO).
2. Open the repo → **Settings** → **Pages**.
3. Under **Build and deployment** → **Source**, choose **Deploy from a branch**.
4. **Branch:** `main` · **Folder:** `/docs` · **Save**.
5. Wait 1–3 minutes. GitHub runs Jekyll with the `github-pages` gem (supports `remote_theme`).
6. Your site URL: **https://scarlettychen.github.io/NODO/**

### After every doc change

```bash
git add docs/
git commit -m "Update docs"
git push origin main
```

GitHub rebuilds automatically. Hard-refresh the browser if you do not see changes.

### Local preview (optional)

Requires Ruby **3.0+**:

```bash
cd docs
bundle install
bundle exec jekyll serve
```

Open http://127.0.0.1:4000/NODO/ (use `--baseurl ""` if testing without project baseurl).

## Java / FTC Blocks toggle

- Header: `_includes/header_custom.html`
- Preference: `localStorage` key `nodo-docs-lang` (`java` | `blocks`), default **java**
- Script: `assets/js/language-toggle.js`
- Styles: `_sass/custom/custom.scss`
- Authoring: see `language-toggle.md`
