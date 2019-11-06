((nil . ((cider-clojure-cli-global-options . "-A:dev:test:ui:shadow-cljs")
         (cider-preferred-build-tool       . clojure-cli)
         (cider-default-cljs-repl          . procflow)
         (cider-repl-display-help-banner   . nil)
         (eval . (progn
                   (add-to-list 'cider-jack-in-nrepl-middlewares
                                "shadow.cljs.devtools.server.nrepl/middleware")

                   (cider-register-cljs-repl-type 'procflow "(user/shadow-cljs-repl)")

                   (defun cider-quit (&optional repl)
                     (interactive)
                     (cl-loop for repl
                              in (cider-repls 'multi)
                              do (cider--close-connection repl))
                     (unless (cider-sessions)
                       (cider-close-ancillary-buffers))
                     (run-hooks 'sesman-post-command-hook)))))))
