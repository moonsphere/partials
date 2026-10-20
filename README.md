# partials

由 Claude Opus 作曲、[Overtone](https://github.com/overtone/overtone) 演奏的音乐。

**Partials**（分音）是组成一个乐音的各个正弦成分：基频加上它上方的泛音。
有一个声学现象叫 *missing fundamental*（缺失基频）：去掉基频、只留泛音，耳朵仍然能听出那个不存在的音。
Opus 听不见声音，它只是把一层层分音写成代码，最后由听的人在脑子里补全旋律。

## 准备

```sh
sudo apt install supercollider-server pipewire-jack   # scsynth + 让它跑在 PipeWire 上
```

还需要 JDK 17+（已在 21 上验证）、[Leiningen](https://leiningen.org) 和 ffmpeg。
`bin/` 下的脚本会优先使用 `/usr/lib/jvm/default-java`，也可以通过 `JAVA_CMD` 指定。

## 用法

```sh
bin/repl                      # 启动 nREPL（ns 为 partials.studio），保持运行
bin/eval "(play! 'op00)"      # 从头播放一首作品
bin/eval "(stop!)"            # 全部静音
bin/render op00               # 录音到 out/op00-tuning.wav
bin/look out/op00-tuning.wav  # 频谱图 + 电平统计 -> out/op00-tuning.png
```

`bin/render` 检测到 REPL 正在运行时会直接复用它，否则会单独起一个 JVM（`lein render op00`）。
如果已经有在跑的 scsynth，设置 `SC_PORT=57110` 就会连上它，不再另外启动一个。

## 作品

每首作品是 `src/partials/pieces/` 下的一个命名空间，按作品号（opus number）命名：

```clojure
(ns ^{:opus 1 :title "…" :duration 90}   ; :duration 单位为秒，录音时会用到
  partials.pieces.op01-something
  "说明这首曲子在做什么。"
  (:refer-clojure :exclude [abs])
  (:use overtone.core))

(defn play [] …)   ; 从现在开始，把整首曲子排进时间线
```

| 作品号 | 标题 | 说明 |
|---|---|---|
| Op. 0 | Tuning | 试音：A1 的前八个分音逐个进入，随后基频消失 |
