(function () {
  'use strict';

  var STORAGE_KEY = 'nodo-docs-lang';
  var DEFAULT_CATEGORY = 'java';
  var VALID_CATEGORIES = { java: true, blocks: true };

  function categorize(label) {
    var value = (label || '').toLowerCase();
    if (value.indexOf('block') !== -1) {
      return 'blocks';
    }
    return 'java';
  }

  function normalize(category) {
    return VALID_CATEGORIES[category] ? category : DEFAULT_CATEGORY;
  }

  function readCategory() {
    try {
      var stored = window.localStorage.getItem(STORAGE_KEY);
      if (VALID_CATEGORIES[stored]) {
        return stored;
      }
    } catch (e) {
      /* ignore */
    }
    return DEFAULT_CATEGORY;
  }

  function writeCategory(category) {
    try {
      window.localStorage.setItem(STORAGE_KEY, category);
    } catch (e) {
      /* ignore */
    }
  }

  function getDirectPanels(group) {
    var panels = [];
    var child = group.firstElementChild;

    while (child) {
      if (child.hasAttribute('data-language')) {
        panels.push(child);
      }
      child = child.nextElementSibling;
    }

    return panels;
  }

  function setPanelVisible(panel, visible) {
    if (!panel) {
      return;
    }

    panel.classList.toggle('is-lang-active', visible);

    if (visible) {
      panel.removeAttribute('hidden');
      panel.removeAttribute('aria-hidden');
    } else {
      panel.setAttribute('hidden', '');
      panel.setAttribute('aria-hidden', 'true');
    }
  }

  /**
   * Apply one language choice to every toggle group on the page
   * (and keep button active states in sync).
   */
  function applyCategory(category) {
    category = normalize(category);
    document.documentElement.setAttribute('data-nodo-lang', category);

    var groups = document.querySelectorAll('.language-toggle[data-nodo-init]');
    for (var i = 0; i < groups.length; i++) {
      applyCategoryToGroup(groups[i], category);
    }

    // Sync every toggle button on the page, including other sections.
    var buttons = document.querySelectorAll('.nodo-lang-toggle__btn[data-lang-category]');
    for (var j = 0; j < buttons.length; j++) {
      var btn = buttons[j];
      var active = btn.getAttribute('data-lang-category') === category;
      btn.classList.toggle('is-active', active);
      btn.setAttribute('aria-pressed', active ? 'true' : 'false');
    }
  }

  function applyCategoryToGroup(group, category) {
    var panels = group._nodoPanels;
    if (!panels || !panels.length) {
      return;
    }

    var activePanel = null;
    for (var i = 0; i < panels.length; i++) {
      var panelCategory = panels[i].getAttribute('data-lang-category');
      if (panelCategory === category) {
        activePanel = panels[i];
        break;
      }
    }
    if (!activePanel) {
      activePanel = panels[0];
    }

    group.setAttribute('data-active-lang', activePanel.getAttribute('data-language') || '');

    for (var j = 0; j < panels.length; j++) {
      setPanelVisible(panels[j], panels[j] === activePanel);
    }
  }

  function buildToggleNav(group, panels) {
    var nav = document.createElement('nav');
    nav.className = 'nodo-lang-toggle';
    nav.setAttribute('role', 'group');
    nav.setAttribute('aria-label', 'Documentation language');

    // One button per language category (java / blocks), shared across all groups.
    var seen = {};
    for (var i = 0; i < panels.length; i++) {
      var panel = panels[i];
      var label = panel.getAttribute('data-language') || ('Option ' + (i + 1));
      var category = categorize(label);
      panel.setAttribute('data-lang-category', category);

      if (seen[category]) {
        continue;
      }
      seen[category] = true;

      var btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'nodo-lang-toggle__btn';
      btn.setAttribute('data-lang-category', category);
      btn.setAttribute('data-language-label', label);
      btn.setAttribute('aria-pressed', 'false');
      // Stable short labels so every section matches.
      btn.textContent = category === 'blocks' ? 'Blocks' : 'Java';
      nav.appendChild(btn);
    }

    group.insertBefore(nav, panels[0]);
    group._nodoNav = nav;
  }

  function initGroup(group) {
    if (group.getAttribute('data-nodo-init') === 'true') {
      return true;
    }

    var panels = getDirectPanels(group);
    if (panels.length < 2) {
      return false;
    }

    group._nodoPanels = panels;
    buildToggleNav(group, panels);
    group.setAttribute('data-nodo-init', 'true');
    return true;
  }

  function setCategory(category) {
    category = normalize(category);
    writeCategory(category);
    applyCategory(category);
  }

  function onClick(event) {
    var btn = event.target.closest('.nodo-lang-toggle__btn[data-lang-category]');
    if (!btn) {
      return;
    }

    event.preventDefault();
    event.stopPropagation();
    setCategory(btn.getAttribute('data-lang-category'));
  }

  function init() {
    var groups = document.querySelectorAll('.language-toggle');
    var initialized = 0;

    for (var i = 0; i < groups.length; i++) {
      if (initGroup(groups[i])) {
        initialized += 1;
      }
    }

    if (!initialized) {
      return;
    }

    applyCategory(readCategory());
    document.documentElement.classList.add('nodo-lang-ready');
    document.addEventListener('click', onClick, true);

    // Keep multiple tabs / pages in sync via localStorage.
    window.addEventListener('storage', function (event) {
      if (event.key === STORAGE_KEY) {
        applyCategory(readCategory());
      }
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
