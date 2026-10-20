(ns partials.studio
  "The room the pieces are played in: boot the server, play a piece, record it.

  A piece is a namespace under partials.pieces whose ns metadata carries
  :opus, :title and :duration (seconds), and which defines a no-arg `play`
  that schedules the whole piece starting now."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [overtone.core :as o]))

(def pieces-dir "src/partials/pieces")

(defn piece-ns
  "Resolve a short id like 'op01 (or a full ns symbol) to the piece's ns symbol."
  [id]
  (let [s (str id)]
    (if (str/includes? s ".")
      (symbol s)
      (let [prefix (str/replace s "-" "_")
            file   (->> (.listFiles (io/file pieces-dir))
                        (map #(.getName ^java.io.File %))
                        (filter #(or (= % (str prefix ".clj"))
                                     (str/starts-with? % (str prefix "_"))))
                        first)]
        (when-not file
          (throw (ex-info (str "No piece matching " s " in " pieces-dir) {:id id})))
        (symbol (str "partials.pieces."
                     (-> file (str/replace #"\.clj$" "") (str/replace "_" "-"))))))))

(defn boot!
  "Connect to scsynth on $SC_PORT if set, otherwise boot one. Idempotent."
  []
  (when-not (o/server-connected?)
    (if-let [port (System/getenv "SC_PORT")]
      (o/connect-server (parse-long port))
      (o/boot-server))
    (o/wait-until-mixer-booted))
  :ready)

(defn load-piece
  "(Re)load a piece and return its ns symbol."
  [id]
  (let [ns (piece-ns id)]
    (require ns :reload)
    ns))

(defn info
  "A piece's ns metadata: :opus, :title, :duration, :doc."
  [id]
  (meta (find-ns (load-piece id))))

(defn play!
  "Reload a piece and start it from the top. Returns its metadata."
  [id]
  (boot!)
  (o/stop)
  (let [ns (load-piece id)]
    ((ns-resolve ns 'play))
    (meta (find-ns ns))))

(defn render!
  "Play a piece from the top and record it to out/<name>.wav. Blocks for the
  piece's :duration plus `tail` seconds so reverbs can ring out.
  Returns the wav path."
  [id & {:keys [tail] :or {tail 3}}]
  (boot!)
  (o/stop)
  (let [ns       (load-piece id)
        duration (:duration (meta (find-ns ns)))
        path     (.getAbsolutePath (io/file "out" (str (last (str/split (str ns) #"\.")) ".wav")))]
    (when-not duration
      (throw (ex-info (str ns " needs :duration in its ns metadata") {:ns ns})))
    (io/make-parents path)
    (o/recording-start path)
    ((ns-resolve ns 'play))
    (Thread/sleep (long (* 1000 (+ duration tail))))
    (o/stop)
    (o/recording-stop)))

(defn stop!
  "Silence everything."
  []
  (o/stop))
