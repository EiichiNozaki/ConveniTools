import { c as h, g as w, a as N, t as D, s as u, P as d, i as P, b as k, d as y, e as m, x as f, f as F, h as R, B as E, m as A, o as b, E as L, j as v, C as g, k as V, l as U } from "./copilot-C2QUmolC.js";
function c(o, t) {
  B(o) ? (D("show-in-ide", { attach: t ?? !1, goToCustomComponentFile: !0 }), u(`${d}show-in-ide`, {
    javaClassName: o.className,
    fileName: o.absoluteFilePath
  })) : P(o) ? (D("show-in-ide", { attach: t ?? !1 }), u(`${d}show-in-ide`, { ...k(o), attach: t ?? !1 })) : (y("show-in-ide"), u(`${d}show-in-ide`, o));
}
h.on("show-in-ide", (o) => {
  const t = o.detail.node;
  if (o.detail.source) {
    c(o.detail.source);
    return;
  }
  if (o.detail.javaSource) {
    c(o.detail.javaSource);
    return;
  }
  if (!t)
    return;
  if (t.isFlowComponent) {
    c(t.node, o.detail.attach);
    return;
  }
  const e = I(t);
  e && c(e);
});
function B(o) {
  return o === void 0 ? !1 : o.className !== void 0 ? !0 : o.absoluteFilePath !== void 0;
}
function I(o) {
  if (!o.isReactComponent)
    return;
  const t = w(o.node);
  if (t)
    return t;
  const e = N(o.node);
  if (e)
    return e;
  const n = o.children.sort((i, r) => i.siblingIndex - r.siblingIndex).find((i) => i.isReactComponent && I(i) !== void 0);
  if (!n)
    throw new Error(`Could not find the source of ${o.nameAndIdentifier}`);
  return w(n.node);
}
const p = "vaadin.copilot.viewCreated";
function C(o) {
  const t = document.createElement("div");
  document.body.innerHTML = "", document.body.appendChild(t), E(o, t);
}
function T() {
  C(
    f`<div class="flex flex-col gap-4 h-screen items-center justify-center">
      <vaadin-icon .svg=${m.rotatingSpinner}></vaadin-icon>
      <h3 class="m-0">The files have been created</h3>
      <p class="m-0">Restart the server to load the new view</p>
      <p class="m-0"><small>The page will refresh automatically when the server is ready.</small></p>
    </div>`
  );
}
async function S() {
  const e = Date.now(), n = async () => {
    try {
      return (await fetch(window.location.href, { method: "HEAD" })).ok;
    } catch {
      return !1;
    }
  };
  let i = !1;
  for (; Date.now() - e < 12e4; ) {
    if (!await n()) {
      i = !0;
      break;
    }
    await new Promise((a) => {
      setTimeout(a, 1e3);
    });
  }
  for (; Date.now() - e < 12e4; ) {
    if (await n() && i) {
      sessionStorage.removeItem(p), window.location.reload();
      return;
    }
    await new Promise((a) => {
      setTimeout(a, 1e3);
    });
  }
}
function x(o) {
  C(
    f`<div class="flex flex-col gap-4 h-screen items-center justify-center">
      <vaadin-icon .svg=${m.rotatingSpinner}></vaadin-icon>
      <h3 class="m-0">Creating your ${o === "flow" ? "Flow" : "Hilla"} view...</h3>
    </div>`
  ), F("copilot-init-app", { framework: o }, async (t) => {
    if (t.data.success)
      sessionStorage.setItem(p, "true"), T(), S();
    else {
      const e = t.data.reason;
      R(e);
    }
  });
}
function j() {
  C(
    f`<div class="m-8">
      <h3>No views found</h3>
      <p>To get started, you can</p>
      <ul>
        <li>
          <a
            href="#"
            @click=${(o) => {
      o.preventDefault(), x("flow");
    }}
            >Create a Flow view using Copilot</a
          >
        </li>
        <li>
          Create a view manually in your IDE, see
          <a target="_blank" href="https://vaadin.com/docs/latest/tutorial">the tutorial</a>
        </li>
      </ul>
      <p>Learn more at <a target="_blank" href="https://vaadin.com/docs">https://vaadin.com/docs</a>.</p>
    </div>`
  );
}
function $() {
  sessionStorage.getItem(p) ? (T(), S()) : j();
}
class W {
  constructor(t) {
    this._currentTree = t;
  }
  get root() {
    return this.currentTree.root;
  }
  get allNodesFlat() {
    return this.currentTree.allNodesFlat;
  }
  getNodeOfElement(t) {
    return this.currentTree.getNodeOfElement(t);
  }
  getChildren(t) {
    return this.currentTree.getChildren(t);
  }
  hasFlowComponents() {
    return this.currentTree.hasFlowComponents();
  }
  findNodeByUuid(t) {
    return this.currentTree.findNodeByUuid(t);
  }
  getElementByNodeUuid(t) {
    return this.currentTree.getElementByNodeUuid(t);
  }
  findByTreePath(t) {
    return this.currentTree.findByTreePath(t);
  }
  get currentTree() {
    return this._currentTree;
  }
  set currentTree(t) {
    this._currentTree = t, h.emit("copilot-tree-created", {});
  }
  get customComponentDataLoaded() {
    return this._currentTree.customComponentDataLoaded;
  }
}
h.on("navigate", (o) => {
  const t = window.history.state?.idx, e = {};
  t !== void 0 && (e.idx = t + 1), window.history.pushState(e, "", o.detail.path), window.dispatchEvent(new PopStateEvent("popstate"));
});
function O(o) {
  const t = window.Vaadin.copilot.tree;
  return o.map((e) => {
    let n = null;
    const { nodeUuid: i, treePath: r, childIndex: a } = e;
    if (i) {
      const l = t.findNodeByUuid(i);
      l && (n = l);
    }
    return n || (n = t.findByTreePath(r) ?? null), n && a !== void 0 && n.children.length > a ? n.children[a] : n;
  }).filter((e) => e !== null);
}
class s {
  constructor() {
    this.drillDownComponentStack = [], A(this, {
      drillDownComponentStack: b.shallow
    });
  }
  getCustomComponentIcon(t) {
    const e = this.getIconTag(t);
    return e === void 0 ? L : m[e];
  }
  getIconTag(t) {
    const n = this.getCustomComponentInfo(t)?.type;
    if (n === "IN_PROJECT")
      return "thermostatCarbon";
    if (n === "EXTERNAL")
      return "deployedCube";
  }
  getCustomComponentInfo(t) {
    if (t.customComponentData && s.isCustomComponentInstanceInfo(t.customComponentData))
      return t.customComponentData;
  }
  isCustomComponent(t) {
    return this.getCustomComponentInfo(t) !== void 0;
  }
  isVisibleAndSelectable(t) {
    if (!this.getTree().customComponentDataLoaded)
      return !0;
    const e = this.getActiveDrillDownContext();
    if (!t.customComponentData)
      return t.isReactComponent && !t.parent && t.name === "App" && !e;
    if (t.uuid === e?.uuid)
      return !0;
    const n = this.getActiveDrillDownData(), i = t.customComponentData;
    return n?.filePath ? t.customComponentData ? this.checkNodeIsInDrillDownContext(i, n) : !1 : i ? !i.childOfCustomComponent : !0;
  }
  pushDrillDownContext(t) {
    this.drillDownComponentStack.length > 0 && this.drillDownComponentStack[this.drillDownComponentStack.length - 1].uuid === t.uuid || (this.drillDownComponentStack.push(t), this.persistIntoStorage(), v(t));
  }
  isDrillDownContext(t) {
    return this.getActiveDrillDownContext()?.uuid === t.uuid;
  }
  getActiveDrillDownContext() {
    if (this.drillDownComponentStack.length !== 0)
      return this.drillDownComponentStack[this.drillDownComponentStack.length - 1];
  }
  clearDrillDownContext() {
    this.drillDownComponentStack = [], this.persistIntoStorage();
  }
  popDrillDownContext() {
    this.drillDownComponentStack.pop(), this.persistIntoStorage();
  }
  hasParentDrillDownContext() {
    return this.drillDownComponentStack.length > 1;
  }
  getParentDrillDownContext() {
    if (this.hasParentDrillDownContext())
      return this.drillDownComponentStack[this.drillDownComponentStack.length - 2];
  }
  isChildInDrillContext(t) {
    const e = t.customComponentData;
    if (!e)
      return !0;
    const n = this.getActiveDrillDownData();
    return n ? this.checkNodeIsInDrillDownContext(e, n) : !1;
  }
  getActiveDrillDownData() {
    const t = this.getActiveDrillDownContext();
    if (t === void 0)
      return;
    const e = this.getCustomComponentInfo(t);
    if (!e?.javaClassName && !e?.reactMethodName)
      return;
    const n = t.node;
    return {
      className: e.javaClassName,
      methodName: e.reactMethodName,
      nodeId: n.nodeId,
      uiId: n.uiId,
      filePath: e.customComponentFilePath ?? void 0
    };
  }
  checkNodeIsInDrillDownContext(t, e) {
    return t.createLocationMethodName && e.methodName ? t.createLocationMethodName === e.methodName && e.filePath === t.createLocationPath : e.filePath === t.createLocationPath && e.className === t.createdClassName;
  }
  persistIntoStorage() {
    const t = this.drillDownComponentStack.map((e) => ({
      treePath: e.path,
      nodeUuid: e.uuid
    }));
    g.saveDrillDownContextReference(t);
  }
  restoreDrillDownFromStorage() {
    const t = g.getDrillDownContextReference();
    let e = [];
    if (t === void 0) {
      const r = this.getTree().allNodesFlat.find((a) => a.customComponentData?.routeView);
      r?.customComponentData && s.isCustomComponentInstanceInfo(r.customComponentData) && (e = [r]);
    } else
      e = O(t);
    e.forEach((r) => {
      const a = this.drillDownComponentStack.findIndex((l) => l.uuid === r.uuid);
      a !== -1 && this.drillDownComponentStack.splice(a, 1), this.drillDownComponentStack.push(r);
    });
    const n = this.drillDownComponentStack.filter(
      (r) => !!this.getTree().findNodeByUuid(r.uuid)
    );
    n.length !== this.drillDownComponentStack.length && (this.drillDownComponentStack = n, this.persistIntoStorage());
    const i = this.getActiveDrillDownContext();
    i && v(i);
  }
  areInternalsVisible(t) {
    if (!this.getCustomComponentInfo(t))
      return !0;
    const n = this.getActiveDrillDownData();
    let i;
    return n && n.filePath && (i = n.filePath), i ? this.checkChildrenCreateLocationToDisplayInternals(t.children, i) : !1;
  }
  checkChildrenCreateLocationToDisplayInternals(t, e) {
    for (const n of t) {
      const i = n.customComponentData;
      if (i && i.createLocationPath === e || this.checkChildrenCreateLocationToDisplayInternals(n.children, e))
        return !0;
    }
    return !1;
  }
  getDescendantsCreatedInActiveDrillDownContextFlatten(t) {
    if (t.customComponentData && s.isCustomComponentInstanceInfo(t.customComponentData)) {
      const e = this.getActiveDrillDownData();
      let n;
      if (e && e.filePath ? n = e.filePath : this.getRouteViewPath() && (n = this.getRouteViewPath()), n)
        return this.getChildrenInPathFlattenRecursively(t, n);
    }
    return [];
  }
  getChildrenInPathFlattenRecursively(t, e) {
    const n = t.children, i = [];
    for (const r of n) {
      const a = r.customComponentData;
      a && a.createLocationPath === e && i.push(r), i.push(...this.getChildrenInPathFlattenRecursively(r, e));
    }
    return i;
  }
  /**
   * Accessed to copilot tree through window object to avoid circular dependency or initialization issues.
   * @private
   */
  getTree() {
    return window.Vaadin.copilot.tree;
  }
  getRouteViewPath() {
    const t = this.getTree().allNodesFlat.find((e) => e.customComponentData?.routeView === !0);
    if (t)
      return t.customComponentData?.createLocationPath ?? void 0;
  }
  static isCustomComponentInstanceInfo(t) {
    return "type" in t && "activeLevel" in t;
  }
}
window.Vaadin.copilot.comm = V;
const _ = new U();
window.Vaadin.copilot.tree = new W(_);
window.Vaadin.copilot.customComponentHandler = new s();
window.Vaadin.copilot.initEmptyApp = x;
window.Vaadin.copilot.noRoutesInProject = $;
