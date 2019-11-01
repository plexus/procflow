(ns procflow.ui.components
  (:refer-clojure :exclude [List])
  (:require [reagent.core :as r]
            ["@material-ui/core/Button" :default Button*]
            ["@material-ui/core/CssBaseline" :default CssBaseline*]
            ["@material-ui/core/AppBar" :default AppBar*]
            ["@material-ui/core/Toolbar" :default Toolbar*]
            ["@material-ui/core/IconButton" :default IconButton*]
            ["@material-ui/core/Typography" :default Typography*]
            ["@material-ui/core/Drawer" :default Drawer*]
            ["@material-ui/core/List" :default List*]
            ["@material-ui/core/ListItem" :default ListItem*]
            ["@material-ui/core/ListItemText" :default ListItemText*]
            ["@material-ui/icons/Menu" :default MenuIcon*]))


(def Button (r/adapt-react-class Button*))
(def CssBaseline (r/adapt-react-class CssBaseline*))
(def AppBar (r/adapt-react-class AppBar*))
(def Toolbar (r/adapt-react-class Toolbar*))
(def IconButton (r/adapt-react-class IconButton*))
(def Typography (r/adapt-react-class Typography*))
(def Drawer (r/adapt-react-class Drawer*))
(def List (r/adapt-react-class List*))
(def ListItem (r/adapt-react-class ListItem*))
(def ListItemText (r/adapt-react-class ListItemText*))

(def MenuIcon (r/adapt-react-class MenuIcon*))
