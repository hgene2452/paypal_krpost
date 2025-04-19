## 1 paypal 결제연동 api

### 1.1 요약

> 서비스 사용자들이 신청한 택배요금을 페이팔로 결제하고, 결제 완료 여부를 서버가 확실하게 알도록 한다.
>

**1**. [앱 등록] 페이팔 개발자 사이트에 앱을 만들고 `Client ID`, `Secret` 확보.

**2** [서버 구축] 백엔드에 아래 API 연동.

- `토큰 발급 API`
- `주문 생성 API`
- `결제 승인(캡처) API`
- `주문 조회 API`

**3**. [테스트] Sandbox 환경에서 결제 테스트.

---

### 2.2 앱 등록

a. Business 회원으로 계정 생성

[PayPal Developer](https://developer.paypal.com/home/)

b. 로그인 후 앱 생성

- 모드(Sandbox : 테스트용, Live : 실제 결제용) → 우측 상단에서 제어 가능.
- 앱 생성 → 상단 네비게이션 바 → Apps & Credentials → Create App.
- 생성된 `Sandbox account`, `Client ID`, `Secret` 정보 저장.

c. PayPal REST API 정리

[Get Started with PayPal REST APIs](https://developer.paypal.com/api/rest/)

```json
Sandbox Server : https://api-m.sandbox.paypal.com
Live Server : https://api-m.paypal.com
```
엑세스 토큰 발급
```bash
curl -v -X POST "https://api-m.sandbox.paypal.com/v1/oauth2/token" \
-u "CLIENT_ID:CLIENT_SECRET" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=client_credentials"

# response
{
  "scope": "https://uri.paypal.com/services/invoicing https://uri.paypal.com/services/disputes/read-buyer https://uri.paypal.com/services/payments/realtimepayment https://uri.paypal.com/services/disputes/update-seller https://uri.paypal.com/services/payments/payment/authcapture openid https://uri.paypal.com/services/disputes/read-seller https://uri.paypal.com/services/payments/refund https://api-m.paypal.com/v1/vault/credit-card https://api-m.paypal.com/v1/payments/.* https://uri.paypal.com/payments/payouts https://api-m.paypal.com/v1/vault/credit-card/.* https://uri.paypal.com/services/subscriptions https://uri.paypal.com/services/applications/webhooks",
  "access_token": "A21AAFEpH4PsADK7qSS7pSRsgzfENtu-Q1ysgEDVDESseMHBYXVJYE8ovjj68elIDy8nF26AwPhfXTIeWAZHSLIsQkSYz9ifg",
  "token_type": "Bearer",
  "app_id": "APP-80W284485P519543T",
  "expires_in": 31668,
  "nonce": "2020-04-03T15:35:36ZaYZlGvEkV4yVSz8g6bAKFoGSEzuy3CQcz3ljhibkOHg"
}
```
결제 주문 생성 요청
```bash
curl -v -X POST https://api-m.sandbox.paypal.com/v2/checkout/orders \
-H 'Content-Type: application/json' \
-H 'PayPal-Request-Id: 7b92603e-77ed-4896-8e78-5dea2050476a' \
-H 'Authorization: Bearer 6V7rbVwmlM1gFZKW_8QtzWXqpcwQ6T5vhEGYNJDAAdn3paCgRpdeMdVYmWzgbKSsECednupJ3Zx5Xd-g' \
-d '{
  "intent": "CAPTURE",
  "payment_source": {
    "paypal": {
      "experience_context": {
        "payment_method_preference": "IMMEDIATE_PAYMENT_REQUIRED",
        "landing_page": "LOGIN",
        "shipping_preference": "GET_FROM_FILE",
        "user_action": "PAY_NOW",
        "return_url": "https://example.com/returnUrl",
        "cancel_url": "https://example.com/cancelUrl"
      }
    }
  },
  "purchase_units": [
    {
      "invoice_id": "90210",
      "amount": {
        "currency_code": "USD",
        "value": "230.00",
        "breakdown": {
          "item_total": {
            "currency_code": "USD",
            "value": "220.00"
          },
          "shipping": {
            "currency_code": "USD",
            "value": "10.00"
          }
        }
      }
    ]
  }'
  
# response
{
  "id": "5O190127TN364715T",
  "status": "PAYER_ACTION_REQUIRED",
  "payment_source": {
    "paypal": {}
  },
  "links": [
    {
      "href": "https://api-m.paypal.com/v2/checkout/orders/5O190127TN364715T",
      "rel": "approve",
      "method": "GET"
    },
    {
      "href": "https://www.paypal.com/checkoutnow?token=5O190127TN364715T",
      "rel": "capture",
      "method": "POST"
    }
  ]
}
```
결제 캡쳐(확정) 요청
```bash
curl -v -X POST https://api-m.sandbox.paypal.com/v2/payments/authorizations/6DR965477U7140544/capture \
-H 'Content-Type: application/json' \
-H 'Authorization: Bearer A21_A.AAeMYAtAqEDt32STD8Yr1eIegfWDQ3IizjYHsmAT5mgwGFeNuBv4xxgRNj8CV5g15oMIjfBwYMrYZKviQIPoLV1lXDmZOw' \
-H 'PayPal-Request-Id: 123e4567-e89b-12d3-a456-426655440010' \
-H 'Prefer: return=representation' \
-d '{}'

# response
{
  "id": "7TK53561YB803214S",
  "amount": {
    "currency_code": "USD",
    "value": "100.00"
  },
  "final_capture": true,
  "seller_protection": {
    "status": "ELIGIBLE",
    "dispute_categories": [
      "ITEM_NOT_RECEIVED",
      "UNAUTHORIZED_TRANSACTION"
    ]
  },
  "seller_receivable_breakdown": {
    "gross_amount": {
      "currency_code": "USD",
      "value": "100.00"
    },
    "paypal_fee": {
      "currency_code": "USD",
      "value": "3.98"
    },
    "net_amount": {
      "currency_code": "USD",
      "value": "96.02"
    },
    "exchange_rate": {}
  },
  "invoice_id": "OrderInvoice-10_10_2024_12_58_20_pm",
  "status": "PENDING",
  "status_details": {
    "reason": "OTHER"
  },
  "create_time": "2024-10-14T21:37:10Z",
  "update_time": "2024-10-14T21:37:10Z",
  "links": [
    {
      "href": "https://api.msmaster.qa.paypal.com/v2/payments/captures/7TK53561YB803214S",
      "rel": "self",
      "method": "GET"
    },
    {
      "href": "https://api.msmaster.qa.paypal.com/v2/payments/captures/7TK53561YB803214S/refund",
      "rel": "refund",
      "method": "POST"
    },
    {
      "href": "https://api.msmaster.qa.paypal.com/v2/payments/authorizations/6DR965477U7140544",
      "rel": "up",
      "method": "GET"
    }
  ]
} 
```
결제 주문 상세정보 요청
```bash
curl -v -X GET https://api-m.sandbox.paypal.com/v2/checkout/orders/5O190127TN364715T \
-H 'Authorization: Bearer 6V7rbVwmlM1gFZKW_8QtzWXqpcwQ6T5vhEGYNJDAAdn3paCgRpdeMdVYmWzgbKSsECednupJ3Zx5Xd-g'  

# response
{
  "id": "5O190127TN364715T",
  "status": "APPROVED",
  "intent": "CAPTURE",
  "payment_source": {
    "paypal": {
      "name": {
        "given_name": "John",
        "surname": "Doe"
      },
      "email_address": "customer@example.com",
      "account_id": "QYR5Z8XDVJNXQ"
    }
  },
  "purchase_units": [
    {
      "reference_id": "d9f80740-38f0-11e8-b467-0ed5f89f718b",
      "amount": {
        "currency_code": "USD",
        "value": "100.00"
      }
    }
  ],
  "payer": {
    "name": {
      "given_name": "John",
      "surname": "Doe"
    },
    "email_address": "customer@example.com",
    "payer_id": "QYR5Z8XDVJNXQ"
  },
  "create_time": "2018-04-01T21:18:49Z",
  "links": [
    {
      "href": "https://api-m.paypal.com/v2/checkout/orders/5O190127TN364715T",
      "rel": "self",
      "method": "GET"
    },
    {
      "href": "https://www.paypal.com/checkoutnow?token=5O190127TN364715T",
      "rel": "approve",
      "method": "GET"
    },
    {
      "href": "https://api-m.paypal.com/v2/checkout/orders/5O190127TN364715T",
      "rel": "update",
      "method": "PATCH"
    },
    {
      "href": "https://api-m.paypal.com/v2/checkout/orders/5O190127TN364715T/capture",
      "rel": "capture",
      "method": "POST"
    }
  ]
}
```

d. 테스트용 계정 생성

- PayPal Korea 정책 : 국내 송금/결제 기능이 막혀있음 (미국 → 한국 결제는 가능).
- Testing Tools → Sandbox Accounts → Create Account
- 생성된 로그인 Info를 통해 이후, 결제 승인 처리
---

### 2.3 서버 구축

```json
계산된 금액으로 PayPal 결제요청 객체 생성
                ↓
결제요청 생성 응답 중 href(approve 링크)로 이동해서 사용자가 결제접수하도록 유도
                ↓
결제 금액,주문ID,사용자ID가 일치한지 검증 → 일치하지 않을 경우, 예외처리
                ↓
검증통과시, 결제 확정(실제 돈이 빠져나가는 시점)
                ↓
금액,주문ID,캡쳐ID,사용자ID 마지막으로 결제 검증 → 일치하지 않을 경우, 예외처리
                ↓
              택배접수
```

---

### 2.4 테스트

a. 계산된 금액으로 PayPal 결제요청

```json
[POST] /api/payments

{
  "intent": "CAPTURE",
  "purchase_units": [
    {
      "amount": {
        "currency_code": "USD",
        "value": "100.00"
      }
    }
  ]
}
```

<img width="614" alt="image (11)" src="https://github.com/user-attachments/assets/db021ba4-99b5-4dde-81b6-a55e6c86d307" />
<br>
<img width="1152" alt="image (12)" src="https://github.com/user-attachments/assets/6dcb2c8c-aff8-4fab-84cf-6c7fc0e74a20" />

- 응답으로 사용자가 결제할 수 있는 URL이 반환됨.
- `DB.PAYMENTS` 테이블에는 결제 엔티티 객체가 저장됨, 이때 `CAPTURE_HREF` 컬럼에는 해당 결제주문에 대한 관리자가 결제확정을 요청해야하는 URL이 저장됨.

b. 결제 금액,주문ID,사용자ID가 일치한지 검증

```json
[GET] /api/payments/59J94462F5257781W
```

<img width="637" alt="image (13)" src="https://github.com/user-attachments/assets/fe13285d-caff-4920-a3fa-e36f418e2dc6" />

- 해당 결제주문(orderId)에 대한 상세정보를 조회해보면, 아직 결제주문이 생성되었지만 결제확정이 나지 않았기 때문에 `CREATED` 상태값 반환.

c. 결제 확정

```json
[POST] api/payments/59J94462F5257781W/capture
```

```json
422 Unprocessable Entity from POST https://api-m.sandbox.paypal.com/v2/checkout/orders/59J94462F5257781W/capture
```

- 만약 사용자가 결제를 하지 않은 상태로 결제 확정처리를 요청하면 422 에러 발생.

<img width="652" alt="image (14)" src="https://github.com/user-attachments/assets/a20139a6-8e33-4332-848b-febb56ac82c5" />

- 결제주문 생성 api 반환값으로 받은 URL을 통해 접근하여 결제.

<img width="637" alt="image (15)" src="https://github.com/user-attachments/assets/8363cc22-02f7-4b09-a788-1c4eb35b17c7" />
<br>
<img width="1193" alt="image (16)" src="https://github.com/user-attachments/assets/2d9b3458-9063-4db4-94de-5b031f9d5847" />

- 이후 다시 결제확정 api를 날려보면 이전에 생성했던 결제주문 객체의 `STATUS`값이 COMPLETED로 변경되고, `CAPTURE_ID`값이 생성되어 저장됨.

d. 금액,주문ID,캡쳐ID,사용자ID 마지막으로 결제 검증

```json
[GET] /api/payments/59J94462F5257781W
```

<img width="637" alt="image (17)" src="https://github.com/user-attachments/assets/4b221350-b628-4e49-bd5d-ac45d36e667c" />

- 그리고 다시 해당 결제주문의 상세정보를 조회해보면, 상태값이 `COMPLETED`로 결제가 잘 완료되었음을 알 수 있음.
