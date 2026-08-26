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

  function findPanelForCategory(panels, category) {
    for (var i = 0; i < panels.length; i++) {
      if (categorize(panels[i].getAttribute('data-language')) === category) {
        return panels[i];
      }
    }
    return panels[0] || null;
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

  function applyCategoryToGroup(group, category) {
    var panels = group._nodoPanels;
    var nav = group._nodoNav;

    if (!panels || !panels.length) {
      return;
    }

    if (!VALID_CATEGORIES[category]) {
      category = DEFAULT_CATEGORY;
    }

    var activePanel = findPanelForCategory(panels, category);
    var activeLabel = activePanel ? activePanel.getAttribute('data-language') : '';

    group.setAttribute('data-active-lang', activeLabel);

    for (var i = 0; i < panels.length; i++) {
      setPanelVisible(panels[i], panels[i] === activePanel);
    }

    if (!nav) {
      return;
    }

    var buttons = nav.querySelectorAll('.nodo-lang-toggle__btn[data-lang-category]');
    for (var j = 0; j < buttons.length; j++) {
      var btn = buttons[j];
      var btnActive = btn.getAttribute('data-lang-category') === category;
      btn.classList.toggle('is-active', btnActive);
      btn.setAttribute('aria-pressed', btnActive ? 'true' : 'false');
    }
  }

  function applyCategory(category) {
    var groups = document.querySelectorAll('.language-toggle[data-nodo-init]');
    for (var i = 0; i < groups.length; i++) {
      applyCategoryToGroup(groups[i], category);
    }
  }

  function buildToggleNav(group, panels) {
    var nav = document.createElement('nav');
    nav.className = 'nodo-lang-toggle';
    nav.setAttribute('role', 'group');
    nav.setAttribute('aria-label', 'Documentation language');

    for (var i = 0; i < panels.length; i++) {
      var panel = panels[i];
      var label = panel.getAttribute('data-language') || ('Option ' + (i + 1));
      var category = categorize(label);

      var btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'nodo-lang-toggle__btn';
      btn.setAttribute('data-lang-category', category);
      btn.setAttribute('data-language-label', label);
      btn.setAttribute('aria-pressed', 'false');
      btn.textContent = label;
      nav.appendChild(btn);
    }

    group.insertBefore(nav, panels[0]);
    group._nodoNav = nav;
    nav._nodoGroup = group;
  }

  function initGroup(group) {
    var panels = getDirectPanels(group);
    if (panels.length < 2) {
      return false;
    }

    group._nodoPanels = panels;
    buildToggleNav(group, panels);
    group.setAttribute('data-nodo-init', 'true');
    return true;
  }

  function onClick(event) {
    var btn = event.target.closest('.nodo-lang-toggle__btn[data-lang-category]');
    if (!btn) {
      return;
    }

    event.preventDefault();
    event.stopPropagation();

    var category = btn.getAttribute('data-lang-category');
    if (!VALID_CATEGORIES[category]) {
      return;
    }

    writeCategory(category);
    applyCategory(category);
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
