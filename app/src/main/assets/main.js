class TextField {
    constructor(opt = {
        name: null,
        id: null,
        className: null,
        prependicon: null,
        appendicon: null,
        placeholder: null,
        description: null,
        size: null,
        value: null,
        color: "default"
    }) {
        let i = createElement({type: (opt.size === "textarea" ? "textarea" : "input")});
        if (opt.prependicon)
            i.setAttribute("data-prependIcon", opt.prependicon);
        if (opt.appendicon)
            i.setAttribute("data-appendIcon", opt.appendicon);
        if (opt.description)
            i.setAttribute("data-textDescription", opt.description);
        if (opt.name)
            i.setAttribute("name", opt.name);
        if (opt.id)
            i.setAttribute("id", opt.id);
        if (opt.className)
            i.setAttribute("class", opt.className);
        if (opt.placeholder)
            i.setAttribute("placeholder", opt.placeholder);

        i.classList.add("form-control");
        let j = createElement({
            type: "div",
            attr: [["class", "form-object" + opt.color]]
        });
        j.appendChild(i);
        return j;
    }
}

class Button {
    constructor(opt = {
        name: null,
        id: null,
        className: null,
        prependicon: null,
        appendicon: null,
        size: null,
        style: null,
        innerHTML: "Button",
        color: "default",
        ripple: true
    }) {
        let i = createElement({type: "button"});
        if (opt.prependicon)
            i.innerHTML = `<span class='mi'>${opt.prependicon}</span>`
        i.innerHTML += opt.innerHTML;
        if (opt.appendicon)
            i.innerHTML = `<span class='mi'>${opt.appendicon}</span>`
        if (opt.name)
            i.setAttribute("name", opt.name);
        if (opt.id)
            i.setAttribute("id", opt.id);
        i.setAttribute("class", `btn ${opt.className ? opt.className : ""} ${opt.size ? "btn-" + opt.size : ""} ${opt.style ? "btn-" + opt.style : ""}`.trim());
        if (opt.placeholder)
            i.setAttribute("placeholder", opt.placeholder);
        if (opt.ripple)
            i.setAttribute("ripple", "");

        return i;
    }
}

(() => {
    window.copy = (value = "", opt = {}) => {
        let input = document.createElement("input");
        input.value = value;
        document.body.appendChild(input);
        input.select();
        input.setSelectionRange(0, value.length);
        document.execCommand("copy");
        input.remove();
    };
    window.$ = (selector = "body", context = document) => {
        return context.querySelectorAll(selector);
    };
    window.createElement = (data = {
        type: "div",
        ns: null,
        innerText: null,
        innerHTML: null,
        attr: [],
        onclick: null
    }) => {
        let el;
        if (data.ns)
            el = document.createElementNS(data.ns, data.type);
        else el = document.createElement(data.type);
        if (data.innerText)
            el.innerText = data.innerText;
        if (data.innerHTML)
            el.innerHTML = data.innerHTML;
        if (data.attr)
            data.attr.forEach(e => {
                el.setAttribute(e[0], e[1])
            });
        if (data.onclick)
            el.onclick = data.onclick;
        return el;
    };
    Element.prototype.bcr = HTMLDocument.prototype.bcr = function () {
        return this.getBoundingClientRect();
    };
    Element.prototype.fadeIn = HTMLDocument.prototype.fadeIn = function (displayMode = "unset", visibility = "visible") {
        this.style.opacity = "1";
        this.style.display = displayMode;
        this.style.visibility = visibility;
        this.style.transition += " opacity 500ms ease-in-out";
    };
    Element.prototype.fadeOut = HTMLDocument.prototype.fadeOut = function (remove = false) {
        this.style.opacity = "0";
        setTimeout(() => {
            this.style.display = "none";
            if (remove)
                this.parentElement.removeChild(this);
        }, 500);
    };
    Element.prototype.on = window.on = HTMLDocument.prototype.on = function (types, func, options = {}) {
        if (!(types || func)) {
            return;
        }
        types = Array.isArray(types) ? types : [types];
        types.forEach(type => {
            this.addEventListener(type, func, options);
        });
    };
    Element.prototype.appendNewChild = function (data = {
        type: "div",
        ns: null,
        innerText: null,
        innerHTML: null,
        attr: [],
        onclick: null
    }) {
        let el = createElement(data);
        this.appendChild(el);
        return el;
    };
    Element.prototype.$ = HTMLDocument.prototype.$ = function (selector) {
        return this.querySelectorAll(selector);
    };
    Array.prototype.last = HTMLCollection.prototype.last = function () {
        return this[this.length - 1];
    };
    HTMLCollection.prototype.forEach = NamedNodeMap.prototype.forEach = Array.prototype.forEach;
    NodeList.prototype.map = HTMLCollection.prototype.map = Array.prototype.map;
    NodeList.prototype.addClass = function (value) {
        for (let i = 0; i < this.length; i++) this[i].classList.add(value);
    };
    NodeList.prototype.removeClass = function (value) {
        for (let i = 0; i < this.length; i++) this[i].classList.remove(value);
    };

    const init = (mutationRecord) => {
        /**
         * @param mutationRecord: Callback of MutationObserve
         *  => mutations: MutationRecord[]
         *      can be call in this function, type should be "childList".
         * @example init({type: "childList", addedNodes: document.body.$("*")});
         *
         * Init Elements, eg. initialized all HTMLButtonElements that [ripple] !== "false", initialized all toggles...
         */
        let changedInf = [],
            attrList = ["ripple", "data-toggle", "data-dismissible", "data-target", "href", "data-carousel", "data-position", "class"];
        mutationRecord = mutationRecord[0];
        if (mutationRecord.type === "attributes" && attrList.indexOf(mutationRecord.attributeName) !== -1)
            changedInf.push([mutationRecord.target, mutationRecord.attributeName]);

        else if (mutationRecord.addedNodes && mutationRecord.addedNodes.length > 0)
            mutationRecord.addedNodes.forEach(node => {
                if (node.attributes && node.classList) {
                    let list = [];
                    attrList.forEach(attr => {
                        if (node.attributes[attr] !== undefined)
                            list.push(attr);
                    });
                    if (node.attributes["ripple"] && list.indexOf("ripple") === -1)
                        list.push("ripple");
                    if (node.classList.contains("alert-dismissible") && list.indexOf("data-dismissible") === -1)
                        list.push("data-dismissible");
                    if (node.classList.contains("carousel") && list.indexOf("data-carousel") === -1) list.push("data-carousel")
                    if (list.length > 0)
                        changedInf.push([node, list]);
                }
            });
        if (changedInf.length > 0)
            changedInf.forEach(detail => {
                if (detail[1].indexOf("ripple") !== -1 && detail[0].attributes["ripple"].value !== "false") {
                    // Ripple Generator
                    let rippleContainer,
                        ripple = detail[0], cleanUp = function (rippler) {
                            rippler.remove();
                        }, addRipple = function (e) {
                            let ripple = this,
                                size = ripple.offsetWidth,
                                pos = () => {
                                    return ripple.getBoundingClientRect();
                                },
                                rippler = document.createElement('span'),
                                x = e.clientX - pos().left - (size / 2),
                                y = e.clientY - pos().top - (size / 2),
                                style = 'top:' + y + 'px; left:' + x + 'px; height: '
                                    + size + 'px; width: ' + size + 'px;';
                            ripple.rippleContainer.appendChild(rippler);
                            rippler.setAttribute('style', style);
                            ripple.addEventListener('mouseup', () => setTimeout(() => {
                                cleanUp(rippler);
                            }, 2000), {once: true});
                        }
                    if (detail[0].attributes["ripple"] !== "false") {
                        rippleContainer = document.createElement('div');
                        rippleContainer.className = 'ripple--container';
                        ripple.addEventListener('mousedown', addRipple);
                        ripple.rippleContainer = rippleContainer;
                        ripple.appendChild(rippleContainer);
                    }
                }
                if (detail[1].indexOf("data-dismissible") !== -1 && detail[0].$(".close").length === 0) {
                    // Dismissible Generator
                    let el = document.createElement("button");
                    el.classList.add("close");
                    el.innerText = "close";
                    el.on("click", () => {
                        detail[0].classList.add("fade-out");
                        setTimeout(() => {
                            detail[0].remove();
                        }, 500);
                    }, {once: true});
                    detail[0].appendChild(el);
                }
                if (detail[1].indexOf("data-carousel") !== -1) {
                    // Carousel Generator
                    let curIndex = 0, prevIndex = 0, imgs = detail[0].$(".carousel-item");
                    const slide = (val) => {
                        if (parseInt(val) === curIndex)
                            return;
                        if (val === "prev" || parseInt(val) < curIndex) {
                            prevIndex = curIndex;
                            imgs[curIndex].classList.add("next");
                            imgs[curIndex].classList.remove("active");
                            curIndex = (val === "prev" ? curIndex === 0 ? (imgs.length - 1) : curIndex - 1 : parseInt(val));
                            imgs[curIndex].classList.add("prev");
                            imgs[curIndex].classList.add("active");
                            setTimeout(() => {
                                imgs[curIndex].classList.remove("prev");
                                imgs[prevIndex].classList.remove("next");
                            }, 500);
                        } else {
                            prevIndex = curIndex;
                            imgs[curIndex].classList.add("prev");
                            imgs[curIndex].classList.remove("active");
                            curIndex = (val === "next" ? curIndex === (imgs.length - 1) ? 0 : curIndex + 1 : parseInt(val));
                            imgs[curIndex].classList.add("next");
                            imgs[curIndex].classList.add("active");
                            setTimeout(() => {
                                imgs[curIndex].classList.remove("next");
                                imgs[prevIndex].classList.remove("prev");
                            }, 500);
                        }
                        indicators.children[prevIndex].classList.remove("active");
                        indicators.children[curIndex].classList.add("active");
                    };
                    let indicators = createElement({type: "div", attr: [["class", "carousel-indicators"]]});
                    for (let i = 0; i < imgs.length; i++)
                        indicators.appendNewChild({
                            type: "div", attr: [["class", "carousel-indicator"]], onclick: () => {
                                slide(i.toString());
                            }
                        });
                    indicators.children[0].classList.add("active");
                    detail[0].appendChild(indicators);
                    detail[0].appendNewChild({
                        type: "div",
                        attr: [["class", "carousel-prevIndicator"]],
                        innerHTML: "<span class='mi'>chevron_left</span>",
                        onclick: () => {
                            slide("prev");
                        }
                    });
                    detail[0].appendNewChild({
                        type: "div",
                        attr: [["class", "carousel-nextIndicator"]],
                        innerHTML: "<span class='mi'>chevron_right</span>",
                        onclick: () => {
                            slide("next");
                        }
                    });
                }
                if (detail[1].indexOf("data-toggle") !== -1 || detail[1].indexOf("data-target") !== -1) {
                    if (detail[0].getAttribute("data-toggle") === "collapse") {
                        // Collapse Generator
                        let target = detail[0].link || detail[0].getAttribute("data-target");
                        target = $(target)[0];
                        if (target === undefined)
                            return;
                        target.classList.remove("collapse");
                        target.classList.add("collapse");
                        detail[0].onclick = () => {
                            if (target.style.maxHeight) {
                                target.style.maxHeight = null;
                                target.style.opacity = "0";
                            } else {
                                target.style.maxHeight = target.style.height || (target.scrollHeight + "px");
                                target.style.opacity = "1";
                            }
                        };
                    } else if (detail[0].getAttribute("data-toggle") === "dropdown") {
                        // Dropdown Genrator
                        let target = detail[0].link || detail[0].getAttribute("data-target");
                        target = $(target)[0];
                        if (target === undefined)
                            return;
                        detail[0].onclick = (ev) => {
                            ev.stopPropagation();
                            let bcr = () => {
                                return detail[0].getBoundingClientRect();
                            };
                            target.style.left = `${bcr().x}px`;
                            target.style.top = `${bcr().y + bcr().height + 12}px`;
                            target.classList.toggle("show");
                            let s = window.on("scroll", () => {
                                if (!target.classList.contains("show")) window.removeEventListener("scroll", s);
                                target.style.left = `${bcr().x}px`;
                                target.style.top = `${bcr().y + bcr().height + 12}px`;
                            });
                            window.on("click", () => {
                                target.classList.remove("show");
                            }, {once: true});
                        };
                    }
                }
                if (detail[1].indexOf("class") !== -1) {
                    if (detail[0].classList.contains("form-object")) {
                        // FormObj Generator
                        let inputs = detail[0].$("input, textarea");
                        if (inputs.length === 0)
                            return; // NO-INPUT
                        let label = detail[0].querySelector(`.text-description`);
                        if (label)
                            label.classList.add("label-description");
                        inputs.forEach(input => {
                            let isTextField = (["email", "number", "password", "search", "tel", "text", "url", "datetime"].indexOf(input.type.toLowerCase()) !== -1) || input.tagName.toLowerCase() === "textarea",
                                isCheck = ["checkbox", "radio"].indexOf(input.type.toLowerCase()) !== -1;
                            // input with attr ["email", "number", "password", "search", "tel", "text", "url", "datetime"] are default single-line input style
                            detail[0].children.forEach(e => {
                                if ((e.$("input").length !== 0 && e.$("textarea").length !== 0) && e !== input)
                                    e.remove();
                            }); // Remove Non-input&&Non-textarea eles in .form-object
                            if (isTextField) {
                                let isCombo = input.dataset.role ? input.dataset.role.toLowerCase() === "combobox" : false,
                                    comboList;
                                if (isCombo) {
                                    comboList = detail[0].querySelector(`[data-name='${input.name}']`);
                                    comboList.parentElement.removeChild(comboList);
                                }

                                if (input.dataset.textdescription)
                                    detail[0].appendNewChild({
                                        type: "div",
                                        attr: [["class", `text-description input-text ${(input.disabled || input.attributes['data-noinput']) ? 'inputted' : ''}`], ["data-init", "true"]],
                                        innerText: input.dataset.textdescription
                                    }); // Append Text-Description (notched label)

                                detail[0].appendNewChild({
                                    type: "div",
                                    attr: [["class", "input-group input-text " + input.dataset.color || "default"], ["data-init", "true"]]
                                }); // Append Input's Wrap

                                if (input.dataset.prependicon) // Prepend-icon
                                    detail[0].children.last().appendNewChild({
                                        type: "div",
                                        attr: [["class", 'input-group-prepend']],
                                        innerHTML: `<div class='input-group-text'><div class='mi'>${input.dataset.prependicon}</div></div>`
                                    })

                                detail[0].children.last().appendChild(input);
                                input.classList.add("form-control");
                                input.classList.add("input-text");

                                if (input.dataset.appendicon) // Append-icon
                                    detail[0].children.last().appendNewChild({
                                        type: "div",
                                        attr: [["class", 'input-group-append']],
                                        innerHTML: `<div class='input-group-text'><div class='mi'>${input.dataset.appendicon}</div></div>`
                                    })

                                if (input.value !== "" && input.dataset.textdescription) // If input value !== "", inputted
                                    detail[0].children[0].classList.add("inputted");

                                input._placeholder = input.placeholder;
                                if (input.dataset.textdescription)
                                    input.placeholder = "";

                                if (detail[0].children[input.dataset.textdescription ? 1 : 0].$(".input-group-prepend").length > 0) {
                                    detail[0].children[0].classList.add("icon-prepend");
                                    input.classList.add("icon-prepend");
                                }
                                let mr = new MutationObserver(() => {
                                    if (input.dataset.textdescription && (input.value !== "" || input.matches(":focus")))
                                        detail[0].children[0].classList.add("inputted");
                                    else
                                        detail[0].children[0].classList.remove("inputted");
                                });
                                mr.observe(input, {attributeFilter: ["value"], attributes: true});
                                let comboScrollListener = () => {
                                    let bcr = () => {
                                        return input.getBoundingClientRect();
                                    }, cbcr = () => {
                                        return comboList.getBoundingClientRect();
                                    };
                                    comboList.style.top = bcr().top - cbcr().height < 0 ? (36 + bcr().height) + "px" : ((-cbcr().height - 12) + "px");
                                    comboList.style.left = "0";
                                }, autoComplete = (ev) => {
                                    if (!isCombo) return;
                                    comboList.$(".hidden").forEach(e => {
                                        e.classList.remove("hidden")
                                    });

                                    let resList = [[], []],
                                        matches = [...comboList.children].map(i => i.textContent),
                                        val = input.value, firstEl;
                                    if (ev && ev.inputType && ev.inputType.includes("delete")) firstEl = true;
                                    matches.forEach((e, index) => {
                                        if (val.toLowerCase() === e.substring(0, val.length).toLowerCase()) {
                                            resList[0].push(e);
                                            resList[1].push(index);
                                            if (!firstEl) {
                                                firstEl = true;
                                                input.value = matches[index];
                                                input.setSelectionRange(val.length, e.length);
                                            }
                                        }
                                    });
                                    for (let i = 0; i < matches.length; i++)
                                        if (resList[1].indexOf(i) === -1 && !input.disabled)
                                            comboList.children[i].classList.add("hidden");

                                    comboScrollListener();
                                }, keypress = e => {
                                    if (e.key.toLowerCase() === "enter") {
                                        input.setSelectionRange(input.value.length, input.value.length);
                                        let matches = [...comboList.children].map(i => i.textContent.toLowerCase());
                                        if (matches.indexOf(input.value.toLowerCase()) !== -1)
                                            comboList.children[matches.indexOf(input.value.toLowerCase())].click();
                                    }
                                };
                                if (!input.attributes["data-noinput"] || input.disabled) {
                                    input.on("keypress", keypress);
                                    input.on("input", autoComplete);
                                } else input.disabled = true;
                                detail[0].on("click", () => {
                                    setTimeout(() => {
                                        document.on("click", () => {
                                            if (isCombo && comboList.classList.contains("show")) {
                                                comboList.classList.remove("show");
                                                window.removeEventListener("scroll", comboScrollListener);
                                            }
                                            if (input.dataset.textdescription && input.value === "" && !input.matches(":focus")) {
                                                detail[0].children[0].classList.remove("inputted");
                                                input.placeholder = "";
                                            }
                                            if (!input.matches(":focus"))
                                                input.parentElement.classList.remove("active");
                                        }, {once: true});
                                    }, 1);
                                    if (!input.disabled)
                                        input.parentElement.classList.add("active");
                                    if (input.dataset.textdescription && !input.disabled) {
                                        detail[0].children[0].classList.add("inputted");
                                        input.focus();
                                        setTimeout(() => {
                                            input.placeholder = input._placeholder;
                                        }, 200); //Wait for animation (textDescription)
                                    }
                                    if (isCombo) {
                                        setTimeout(() => {
                                            if (input.value === "")
                                                comboList.$(".hidden").forEach(e => {
                                                    e.classList.remove("hidden")
                                                });
                                            comboList.classList.add("show");
                                            comboScrollListener();
                                            window.on("scroll", comboScrollListener);
                                        }, 200);
                                    }
                                });

                                if (isCombo) {
                                    detail[0].appendChild(comboList);
                                    comboList.classList.add("form-control");
                                    comboList.classList.add("combo-list");
                                    comboList.children.forEach((el) => {
                                        el.on("click", (ev) => {
                                            ev.stopPropagation();
                                            input.value = el.textContent;
                                            comboList.$(".active")[0] ? comboList.$(".active")[0].classList.remove("active") : void (0);
                                            el.classList.add("active");
                                            comboList.classList.remove("show");
                                            window.removeEventListener("scroll", comboScrollListener);
                                            input.blur();
                                            autoComplete();
                                        });
                                    });
                                }

                            } else if (isCheck) {
                                detail[0].appendNewChild({
                                    type: "div",
                                    attr: [["class", "input-group input-checkbox " + input.dataset.color || "default"], ["data-init", "true"]]
                                }); // Append Input's Wrap

                                detail[0].children.last().appendChild(input);
                                input.classList.add("form-control");
                                input.classList.add("input-checkbox");

                                if (input.dataset.textdescription)
                                    detail[0].children.last().appendNewChild({
                                        type: "div",
                                        attr: [["class", "text-description input-checkbox"], ["data-init", "true"]],
                                        innerText: input.dataset.textdescription
                                    });

                                input.on("click", (ev) => {
                                    ev.stopPropagation();
                                    input.removeAttribute("indeterminate");
                                    if (input.checked) {
                                        input.classList.add("inputted");
                                    } else
                                        input.classList.remove("inputted");
                                });

                                detail[0].children.last().on("click", (ev) => {
                                    ev.stopPropagation();
                                    input.click();
                                });
                            }
                        });
                    }
                }
                if (detail[1].indexOf("data-role")) {
                    if (detail[0].dataset.role === "link")
                        detail[0].on("click", () => {
                            window.location.href = detail[0].dataset.target;
                        });
                }
            });
    };
    const mutationObserver = new MutationObserver(init);
    window.on("load", () => {
        init([{type: "childList", addedNodes: $("*")}]);
        mutationObserver.observe(document, {subtree: true, childList: true, attributes: true});
    });
})();