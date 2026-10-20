(defproject partials "0.1.0-SNAPSHOT"
  :description "Music written by Claude Opus, played by Overtone."
  :url "https://github.com/moonsphere/partials"
  :dependencies [[org.clojure/clojure "1.12.3"]
                 [overtone "0.16.3331" :exclusions [net.java.dev.jna/jna]]
                 [net.java.dev.jna/jna "5.19.1"]]
  :jvm-opts ["-Xmx2g"]
  :repl-options {:init-ns partials.studio}
  :aliases {"render" ["run" "-m" "partials.render"]})
