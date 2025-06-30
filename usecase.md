@startuml
left to right direction
skinparam packageStyle rectangle
skinparam actorStyle awesome

actor "購入者" as Customer
actor "ゲストユーザー" as Guest
actor "会員ユーザー" as Member
actor "管理者" as Admin
actor "サイト管理者" as SiteAdmin

package "購入者機能" {
    usecase "商品を閲覧" as UC1
    usecase "商品を検索" as UC2
    usecase "商品をカートに追加" as UC3
    usecase "カート内の商品管理" as UC4
    usecase "注文を確定" as UC5
    usecase "注文完了メールを受信" as UC6
    usecase "問い合わせフォーム利用" as UC7
    usecase "Cookie同意ポップアップ確認" as UC8
}

package "ゲストユーザー機能" {
    usecase "購入可能" as UC9
}

package "会員ユーザー機能" {
    usecase "ログイン／ログアウト" as UC10
    usecase "購入履歴を確認" as UC11
}

package "管理者機能" {
    usecase "商品登録・編集・削除" as UC12
    usecase "在庫数管理" as UC13
    usecase "商品カテゴリ設定" as UC14
    usecase "セール価格設定" as UC15
    usecase "注文一覧を確認" as UC16
    usecase "注文詳細確認" as UC17
    usecase "発送ステータス更新" as UC18
    usecase "CSV一括登録・出力" as UC19
}

package "サイト管理機能" {
    usecase "サイト基本情報の編集" as UC20
    usecase "問い合わせ対応" as UC21
    usecase "Cookie同意バナー設定" as UC22
    usecase "スマホ表示確認" as UC23
}

' アクターとユースケースの関係

Customer --> UC1
Customer --> UC2
Customer --> UC3
Customer --> UC4
Customer --> UC5
Customer --> UC6
Customer --> UC7
Customer --> UC8

Guest --> UC1
Guest --> UC4
Guest --> UC9

Member --> UC1
Member --> UC5
Member --> UC10
Member --> UC11

Admin --> UC12
Admin --> UC13
Admin --> UC14
Admin --> UC15
Admin --> UC16
Admin --> UC17
Admin --> UC18
Admin --> UC19

SiteAdmin --> UC20
SiteAdmin --> UC21
SiteAdmin --> UC22
SiteAdmin --> UC23

@enduml