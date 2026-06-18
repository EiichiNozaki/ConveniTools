import { f as u, a1 as o, R as v, e as i, x as c, r as p, q as m, al as b, p as f } from "./copilot-C2QUmolC.js";
import { B as x } from "./base-panel-Cv9MW3Do.js";
function y(e) {
  return u("copilot-switch-user", { username: e }, (s) => s.data.error ? { success: !1, errorMessage: s.data.error.message } : { success: !0 });
}
var w = Object.defineProperty, U = Object.getOwnPropertyDescriptor, l = (e, s, a, n) => {
  for (var t = n > 1 ? void 0 : n ? U(s, a) : s, d = e.length - 1, h; d >= 0; d--)
    (h = e[d]) && (t = (n ? h(s, a, t) : h(t)) || t);
  return n && t && w(s, a, t), t;
};
let r = class extends x {
  constructor() {
    super(...arguments), this.username = "", this.errorMessage = "", this.isLoading = !1, this.handleKeyDown = async (e) => {
      e.key === "Enter" && this.username && !this.isLoading && await this.handleSwitchUser();
    }, this.handleSwitchUser = async () => {
      if (!(!this.username || this.isLoading)) {
        this.isLoading = !0, this.errorMessage = "";
        try {
          const e = await y(this.username);
          e.success ? (o.addRecentSwitchedUsername(this.username), globalThis.location.reload()) : (this.errorMessage = e.errorMessage, this.isLoading = !1);
        } catch {
          this.errorMessage = "An unexpected error occurred", this.isLoading = !1;
        }
      }
    }, this.switchToRecentUser = async (e) => {
      this.username = e, await this.handleSwitchUser();
    }, this.removeRecentUser = (e, s) => {
      s.stopPropagation(), o.removeRecentSwitchedUsername(e), this.requestUpdate();
    };
  }
  connectedCallback() {
    super.connectedCallback(), this.classList.add("contents"), this.reaction(
      () => o.getRecentSwitchedUsernames(),
      () => {
        this.requestUpdate();
      }
    );
  }
  render() {
    if (!v.springSecurityEnabled)
      return c`
        <div class="flex flex-col items-center pb-4 px-4">
          <vaadin-icon class="icon-lg mb-2" .svg="${i.accountCircle}"></vaadin-icon>
          <h3 class="mb-0.5 mt-0 text-semibold text-sm">Spring Security Disabled</h3>
          <p class="m-0 text-balance text-center text-secondary text-xs">
            User impersonation requires Spring Security to be configured in your application
          </p>
        </div>
      `;
    const e = o.getRecentSwitchedUsernames();
    return c`
      <div class="flex flex-col gap-4 pb-4 px-4">
        <div class="flex gap-4 items-baseline">
          <vaadin-text-field
            class="flex-1"
            label="Username"
            .value="${this.username}"
            .errorMessage="${this.errorMessage}"
            .invalid="${this.errorMessage !== ""}"
            ?disabled="${this.isLoading}"
            @value-changed="${(s) => {
      this.username = s.detail.value, this.errorMessage = "";
    }}"
            @keydown="${this.handleKeyDown}">
            <vaadin-icon slot="prefix" .svg="${i.accountCircle}"></vaadin-icon>
          </vaadin-text-field>
          <vaadin-button
            theme="primary"
            ?disabled="${!this.username || this.isLoading}"
            @click="${this.handleSwitchUser}">
            <vaadin-icon slot="prefix" .svg="${i.swapHoriz}"></vaadin-icon>
            ${this.isLoading ? "Switching..." : "Switch User"}
          </vaadin-button>
        </div>

        ${e.length > 0 ? c`
              <div class="flex flex-col gap-2 mt-1">
                <h3 class="m-0 text-semibold text-sm">Recent Usernames</h3>
                <ul
                  class="bg-gray-2 dark:bg-gray-6 border border-gray-3 dark:border-gray-7 divide-y list-none m-0 p-0 rounded-md">
                  ${e.map(
      (s) => c`
                      <li class="flex gap-1 items-center pe-1 ps-3 py-1">
                        <span class="flex-1">${s}</span>
                        <vaadin-button theme="icon tertiary" @click="${() => this.switchToRecentUser(s)}">
                          <vaadin-icon .svg="${i.swapHoriz}"></vaadin-icon>
                          <vaadin-tooltip slot="tooltip" text="Switch to ${s}"></vaadin-tooltip>
                        </vaadin-button>
                        <vaadin-button
                          aria-label="Remove ${s}"
                          class="text-ruby-11"
                          theme="icon tertiary"
                          @click="${(a) => this.removeRecentUser(s, a)}">
                          <vaadin-icon .svg="${i.delete}"></vaadin-icon>
                          <vaadin-tooltip slot="tooltip" text="Remove ${s}"></vaadin-tooltip>
                        </vaadin-button>
                      </li>
                    `
    )}
                </ul>
              </div>
            ` : ""}
      </div>
    `;
  }
};
l([
  p()
], r.prototype, "username", 2);
l([
  p()
], r.prototype, "errorMessage", 2);
l([
  p()
], r.prototype, "isLoading", 2);
r = l([
  f("copilot-impersonator")
], r);
const g = {
  header: "Impersonate User",
  tag: b.IMPERSONATOR,
  individual: !0,
  toolbarOptions: {
    allowedModesWithOrder: {
      common: 0
    },
    iconKey: "accountCircle"
  }
}, $ = {
  init(e) {
    e.addPanel(g);
  }
};
globalThis.Vaadin.copilot.plugins.push($);
m.addPanel(g);
export {
  r as CopilotImpersonatorPanel
};
