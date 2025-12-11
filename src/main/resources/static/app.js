// app.js (ES module)
const BASE_URL = window.location.origin;

// ---------- helpers ----------
function q(selector, root = document) { return root.querySelector(selector); }
function qa(selector, root = document) { return Array.from(root.querySelectorAll(selector)); }

function buildQuery(params = {}) {
    const parts = [];
    for (const [k, v] of Object.entries(params)) {
        if (v == null) continue;
        if (Array.isArray(v)) {
            v.forEach(item => { if (item != null && item !== '') parts.push(`${encodeURIComponent(k)}=${encodeURIComponent(item)}`); });
        } else {
            if (v !== '') parts.push(`${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`);
        }
    }
    return parts.length ? `?${parts.join('&')}` : '';
}

async function apiGet(path) {
    const res = await fetch(`${BASE_URL}${path}`);
    if (!res.ok) {
        const text = await res.text().catch(()=>res.statusText);
        throw new Error(`${res.status} ${res.statusText}: ${text}`);
    }
    return res.json();
}

// ---------- DOM refs ----------
const catalogRoot = q('#catalog');
const emptyNode = q('#empty');
const searchInput = q('#search-input');

const familiesList = q('#families-list');
const colorsList = q('#colors-list');
const careTagsList = q('#caretags-list');
const minPriceInput = q('#min-price');
const maxPriceInput = q('#max-price');
const applyBtn = q('#apply-filters');
const resetBtn = q('#reset-filters');

// ---------- state ----------
let stateFilters = {
    name: '',
    minPrice: undefined,
    maxPrice: undefined,
    familyNames: [],
    colorNames: [],
    careTagNames: []
};

// ---------- lazy modal ----------
let detailModal = null;
let detailBody = null;

async function ensureDetailModal() {
    if (detailModal) return;

    detailModal = document.createElement('div');
    detailModal.className = 'modal';
    detailModal.innerHTML = `
        <div class="modal-content">
            <button class="close-btn" title="Закрыть">✕</button>
            <div id="detail-body"></div>
        </div>
    `;
    document.body.appendChild(detailModal);

    detailBody = detailModal.querySelector('#detail-body');
    const closeDetailBtn = detailModal.querySelector('.close-btn');

    closeDetailBtn.addEventListener('click', removeDetail);

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && detailModal) removeDetail();
    });
}

function removeDetail() {
    if (detailModal) {
        detailModal.remove();
        detailModal = null;
        detailBody = null;
    }
}

async function showDetail(id, colorId) {
    if (!id || !colorId) return;

    await ensureDetailModal();

    detailBody.innerHTML = '<div>Загрузка...</div>';

    try {
        const data = await apiGet(`/catalog/flower/${id}/${colorId}`);
        renderDetail(data);
    } catch (err) {
        detailBody.innerHTML = `<div class="empty">Ошибка: ${escapeHtml(err.message)}</div>`;
        setTimeout(removeDetail, 3000);
    }
}

function renderDetail(flower) {
    // Собираем все фото для карусели
    const extras = Array.isArray(flower.photos) ? flower.photos :
        Array.isArray(flower.photoUrls) ? flower.photoUrls :
            Array.isArray(flower.images) ? flower.images :
                Array.isArray(flower.otherPhotoUrls) ? flower.otherPhotoUrls : [];
    const primary = flower.photoUrl || '';
    const normalized = primary ? [primary, ...extras.filter(p => p && p !== primary)] : extras.filter(Boolean);
    if (normalized.length === 0) normalized.push('');

    detailBody.innerHTML = `
        <h2 style="margin:0 0 8px 0">${escapeHtml(flower.name)}</h2>
        <div id="detail-carousel" style="position:relative;">
            <img id="detail-main-image" class="detail-image" src="${escapeHtml(normalized[0])}" alt="${escapeHtml(flower.name)}" loading="lazy" />
            ${normalized.length > 1 ? `
                <button id="carousel-prev" aria-label="Предыдущее" title="Предыдущее" style="position:absolute;left:8px;top:50%;transform:translateY(-50%);z-index:2;border:0;background:rgba(255,255,255,0.8);padding:8px;border-radius:999px;cursor:pointer">‹</button>
                <button id="carousel-next" aria-label="Следующее" title="Следующее" style="position:absolute;right:8px;top:50%;transform:translateY(-50%);z-index:2;border:0;background:rgba(255,255,255,0.8);padding:8px;border-radius:999px;cursor:pointer">›</button>
            ` : ''}
        </div>
        <div class="detail-grid">
            <div>
                <p><strong>Семейство:</strong> ${escapeHtml(flower.familyName || '')}</p>
                <p><strong>Цена:</strong> ${formatPrice(flower.price)}</p>
                <p style="margin-top:8px">${escapeHtml(flower.description || '')}</p>
                ${normalized.length > 1 ? `
                    <div style="margin-top:12px; display:flex; gap:8px; flex-wrap:wrap;">
                        ${normalized.map((p, i) => `
                            <button data-thumb-index="${i}" class="thumb-btn" style="border:0;padding:0;background:transparent;cursor:pointer">
                                <img src="${escapeHtml(p)}" alt="${escapeHtml(flower.name)}" style="width:64px;height:48px;object-fit:cover;border-radius:6px;"/>
                            </button>
                        `).join('')}
                    </div>
                ` : ''}
            </div>
            <div>
                <div class="detail-box">
                    <div><strong>Цвета</strong></div>
                    <div style="margin-top:8px">${(flower.colorNames || []).map(c=>`<span class="tag">${escapeHtml(c)}</span>`).join('')}</div>
                    <div style="margin-top:12px"><strong>Особенности ухода</strong></div>
                    <div style="margin-top:8px">${(flower.careTags || []).map(t=>`<span class="tag">${escapeHtml(t)}</span>`).join('')}</div>
                </div>
            </div>
        </div>
    `;

    // Логика управления каруселью
    if (normalized.length > 1) {
        const mainImg = detailBody.querySelector('#detail-main-image');
        const prevBtn = detailBody.querySelector('#carousel-prev');
        const nextBtn = detailBody.querySelector('#carousel-next');
        const thumbBtns = Array.from(detailBody.querySelectorAll('.thumb-btn'));
        let currentIndex = 0;

        const updateMain = () => {
            mainImg.src = normalized[currentIndex] || '';
            thumbBtns.forEach(b => b.removeAttribute('aria-current'));
            const active = thumbBtns.find(b => Number(b.dataset.thumbIndex) === currentIndex);
            if (active) active.setAttribute('aria-current', 'true');
        };

        prevBtn.addEventListener('click', () => {
            currentIndex = (currentIndex - 1 + normalized.length) % normalized.length;
            updateMain();
        });

        nextBtn.addEventListener('click', () => {
            currentIndex = (currentIndex + 1) % normalized.length;
            updateMain();
        });

        thumbBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const i = Number(btn.dataset.thumbIndex);
                if (Number.isFinite(i)) {
                    currentIndex = i;
                    updateMain();
                }
            });
        });

        updateMain();
    }
}

// ---------- init ----------
async function init() {
    if (window.location.pathname === '/' || window.location.pathname === '') {
        window.location.href = '/catalog';
        return;
    }

    bindEvents();
    await loadFilterOptions();
    triggerSearch();
}

// ---------- bind events ----------
function bindEvents() {
    searchInput.addEventListener('input', debounce(e => {
        stateFilters.name = e.target.value.trim();
        triggerSearch();
    }, 350));

    applyBtn.addEventListener('click', () => {
        stateFilters.minPrice = toNum(minPriceInput.value);
        stateFilters.maxPrice = toNum(maxPriceInput.value);
        stateFilters.familyNames = getCheckedValues(familiesList);
        stateFilters.colorNames = getCheckedValues(colorsList);
        stateFilters.careTagNames = getCheckedValues(careTagsList);
        triggerSearch();
    });

    resetBtn.addEventListener('click', () => {
        stateFilters = { name:'', minPrice:undefined, maxPrice:undefined, familyNames:[], colorNames:[], careTagNames:[] };
        searchInput.value = '';
        minPriceInput.value = '';
        maxPriceInput.value = '';
        uncheckAll(familiesList);
        uncheckAll(colorsList);
        uncheckAll(careTagsList);
        triggerSearch();
    });

    catalogRoot.addEventListener('click', (ev) => {
        const a = ev.target.closest('[data-detail]');
        if (!a) return;
        ev.preventDefault();
        const [id, colorId] = a.dataset.detail.split('|');
        showDetail(id, colorId);
    });
}

// ---------- search ----------
let pendingSearch = null;
function triggerSearch() {
    if (pendingSearch) clearTimeout(pendingSearch);
    pendingSearch = setTimeout(doSearch, 250);
}

async function doSearch() {
    pendingSearch = null;
    const params = {};
    if (stateFilters.name) params.name = stateFilters.name;
    if (stateFilters.minPrice != null) params.minPrice = stateFilters.minPrice;
    if (stateFilters.maxPrice != null) params.maxPrice = stateFilters.maxPrice;
    if (stateFilters.familyNames.length) params.familyNames = stateFilters.familyNames;
    if (stateFilters.colorNames.length) params.colorNames = stateFilters.colorNames;
    if (stateFilters.careTagNames.length) params.careTagNames = stateFilters.careTagNames;

    const query = buildQuery(params);
    catalogRoot.innerHTML = '<div>Загрузка...</div>';

    try {
        const list = await apiGet(`/catalog/search-flowers${query}`);
        renderCards(list);
    } catch (err) {
        catalogRoot.innerHTML = `<div class="empty">Ошибка при загрузке: ${escapeHtml(err.message)}</div>`;
    }
}

function renderCards(list) {
    catalogRoot.innerHTML = '';
    if (!list || !list.length) {
        emptyNode.hidden = false;
        return;
    }
    emptyNode.hidden = true;

    const seen = new Set();
    for (const c of list) {
        if (!c || c.id == null) continue;
        const id = String(c.id);
        if (seen.has(id)) continue;
        seen.add(id);

        const card = document.createElement('article');
        card.className = 'card';
        card.innerHTML = `
            <img src="${escapeHtml(c.photoUrl || '')}" alt="${escapeHtml(c.name)}" loading="lazy" />
            <div class="card-body">
                <h3 class="card-title">${escapeHtml(c.name)}</h3>
                <div class="card-row">
                    <div class="price">${formatPrice(c.price)}</div>
                    <a href="#" data-detail="${c.id}|${c.colorId}" class="details-link">Подробнее</a>
                </div>
            </div>
        `;
        catalogRoot.appendChild(card);
    }
}

// ---------- filters ----------
async function loadFilterOptions() {
    try {
        const [families, colors, careTags, priceRange] = await Promise.allSettled([
            apiGet('/catalog/all-families'),
            apiGet('/catalog/all-colors'),
            apiGet('/catalog/all-care-tags'),
            apiGet('/catalog/price-range')
        ]);

        if (families.status === 'fulfilled') fillChecklist(familiesList, families.value);
        if (colors.status === 'fulfilled') fillChecklist(colorsList, colors.value);
        if (careTags.status === 'fulfilled') fillChecklist(careTagsList, careTags.value);
        if (priceRange.status === 'fulfilled') {
            const pr = priceRange.value;
            if (pr && pr.min != null) minPriceInput.placeholder = `min (${pr.min})`;
            if (pr && pr.max != null) maxPriceInput.placeholder = `max (${pr.max})`;
        }
    } catch (err) {
        console.warn('Не удалось загрузить опции фильтров', err);
    }
}

function fillChecklist(container, items = []) {
    container.innerHTML = '';
    if (!items.length) {
        container.innerHTML = '<div class="empty">—</div>';
        return;
    }
    items.forEach(it => {
        const id = `chk-${Math.random().toString(36).slice(2,8)}`;
        const label = document.createElement('label');
        label.innerHTML = `<input id="${id}" type="checkbox" value="${escapeHtml(it)}"> <span>${escapeHtml(it)}</span>`;
        container.appendChild(label);
    });
}

function getCheckedValues(container) {
    return Array.from(container.querySelectorAll('input[type=checkbox]:checked')).map(i => i.value);
}

function uncheckAll(container) {
    Array.from(container.querySelectorAll('input[type=checkbox]')).forEach(i => i.checked = false);
}

// ---------- utils ----------
function toNum(val) {
    if (val == null || String(val).trim() === '') return undefined;
    const n = Number(val);
    return Number.isFinite(n) ? n : undefined;
}

function formatPrice(p) {
    if (p == null) return '—';
    const num = Number(p);
    if (!Number.isFinite(num)) return String(p);
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        maximumFractionDigits: 2
    }).format(num);
}

function escapeHtml(s = '') {
    return String(s)
        .replaceAll('&','&amp;')
        .replaceAll('<','&lt;')
        .replaceAll('>','&gt;')
        .replaceAll('"','&quot;')
        .replaceAll("'",'&#039;');
}

function debounce(fn, delay){
    let t;
    return (...a)=>{
        clearTimeout(t);
        t = setTimeout(()=>fn(...a), delay);
    };
}

// ---------- start ----------
document.addEventListener('DOMContentLoaded', init);