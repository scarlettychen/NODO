(function () {
  'use strict';

  var STORAGE_KEY = 'nodo-docs-lang';
  var DEFAULT_LANG = 'java';
  var VALID = { java: true, blocks: true };

  function readLang() {
    try {
      var stored = window.localStorage.getItem(STORAGE_KEY);
      if (VALID[stored]) {
        return stored;
      }
    } catch (e) {
      /* ignore */
    }
    return DEFAULT_LANG;
  }

  function writeLang(lang) {
    try {
      window.localStorage.setItem(STORAGE_KEY, lang);
    } catch (e) {
      /* ignore */
    }
  }

  function applyLang(lang) {
    if (!VALID[lang]) {
      lang = DEFAULT_LANG;
    }

    document.documentElement.setAttribute('data-docs-lang', lang);

    var buttons = document.querySelectorAll('.nodo-lang-toggle__btn[data-language]');
    for (var i = 0; i < buttons.length; i++) {
      var btn = buttons[i];
      var active = btn.getAttribute('data-language') === lang;
      btn.classList.toggle('is-active', active);
      btn.setAttribute('aria-pressed', active ? 'true' : 'false');
    }
  }

  function onClick(event) {
    var btn = event.target.closest('.nodo-lang-toggle__btn[data-language]');
    if (!btn) {
      return;
    }
    event.preventDefault();
    var lang = btn.getAttribute('data-language');
    if (!VALID[lang]) {
      return;
    }
    writeLang(lang);
    applyLang(lang);
  }

  function init() {
    applyLang(readLang());
    document.addEventListener('click', onClick);

    window.addEventListener('storage', function (event) {
      if (event.key === STORAGE_KEY) {
        applyLang(readLang());
      }
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
