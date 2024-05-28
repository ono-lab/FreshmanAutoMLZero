# AutoMLZero 新人ゼミ

本リポジトリは、三嶋の研究テーマである AutoML-Zero の新人ゼミ用のリポジトリです。

## 内容

新人ゼミのコンテンツとして以下を用意しております。

- 研究の背景や目的の説明
- AutoML-Zero の問題設定
- Esteban らの既存手法 RE-AutoML-Zero について
- 三嶋が卒論の際に提案した MGG-AutoML-Zero について
- 新人ゼミ用の課題

## 資料

資料は[こちら](/doc/main.pdf)からご利用頂けます。

## 課題

### STEP1

MGG-AutoML-Zero+AV（提案手法）のプログラムの一部を穴埋めして実装しよう。

`src/main/java/methods/MGG_AV/TMGGAutoMLZeroAV.java`で`// TODO:`から始まるコメントを検索して実装してください。

利用する関数とその説明を以下に示すので参考にして実装してください。

- TPopulation クラスの randomRemove 関数：集団からランダムに個体を取り出す関数
- TMGGAutoMLZeroAV クラスの addBest2ChildrenOf 関数:親個体から子個体を生成し、家族から Best2 を集団に戻す関数
- TAlgorithmValidator クラスの validate 関数：妥当なアルゴリズムの時に`true`返却する関数

### STEP2

既存手法を線形回帰アルゴリズム探索問題で動かして、最適解の発見に失敗することを確認しよう。
`src/main/java/methods/RE/TLinearRegressionExperiment.java`を実行することで既存手法で線形回帰アルゴリズムを動かせます。

### STEP3

穴埋めしたプログラムを使って、提案手法を動かして線形回帰アルゴリズムの探索問題を動かして、最適解の発見をしよう。
`src/main/java/methods/MGG_AV/TLinearRegressionExperiment.java`を実行することで提案手法で線形回帰アルゴリズムを動かせます。

穴埋めしていないとコンパイルできません。

### STEP4

見つかった最適解を分析して、線形回帰アルゴリズムが得られていることを確認しよう。

## 提出物

以下の 2 つを Slack で送ってください。

- 穴埋めした`src/main/java/methods/MGG_AV/TMGGAutoMLZeroAV.java`ファイル
- 提案手法で見つかったアルゴリズムの Predict 関数と Learn 関数の説明
