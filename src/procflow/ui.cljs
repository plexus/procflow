(ns procflow.ui
  (:require-macros [procflow.ui-macros :refer [datascript-schema]])
  (:require [reagent.core :as r]
            [procflow.ui.components :refer [Button
                                            CssBaseline
                                            AppBar
                                            Toolbar
                                            IconButton
                                            Typography
                                            Drawer
                                            List
                                            ListItem
                                            ListItemText
                                            MenuIcon]]
            [datascript.core :as d]
            [re-posh.core :as posh]))


(set! *warn-on-infer* true)

(def state (r/atom {}))

(def conn (d/create-conn (datascript-schema)))
(posh/connect! conn)

(defn main []
  (js/console.log (pr-str @state))
  [:<>
   [CssBaseline]
   [AppBar {:position "static"}
    [Toolbar
     [IconButton {:edge "start"
                  :color "inherit"
                  :aria-label "menu"
                  :on-click #(swap! state update :sidebar-open? not)}
      [MenuIcon]]
     [Typography {:variant "h6"} "Procedures"]]]
   [Drawer {:open (boolean (:sidebar-open? @state)) :on-close #(swap! state assoc :sidebar-open? false)}
    [List
     [ListItem
      [ListItemText "Hello"]]]]
   [Button {:variant "contained" :color "primary"} "Hello"]
   #_[mui/css-baseline]
   ;; mui-pickers-utils-provider provides date handling utils to date and time pickers.
   ;; cljs-time-utils is an utility package that allows you to use cljs-time / goog.date date objects.
   #_[pickers/mui-pickers-utils-provider {:utils  cljs-time-utils
                                          :locale goog.i18n.DateTimeSymbols_en_US}
      [styles/theme-provider (styles/create-mui-theme custom-theme)
       [mui/grid
        {:container true
         :direction "row"
         :justify   "center"}
        [mui/grid
         {:item true
          :xs   6}
         [(with-custom-styles form)]]]]]])

(defn start []
  (r/render [main] (js/document.getElementById "app")))

(start)
