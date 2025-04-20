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

---

## 3 paypal invoice 연동

> PayPal Order를 생성한 결과 결제 URL과 함께 사용자 이메일로 결제요청을 보내야 한다.
> 

### 3.1 요약
```json
인보이스 드래프트 생성 (POST /v2/invoicing/invoices)
↓
인보이스 발송 (POST /v2/invoicing/invoices/{invoice_id}/send)
↓
고객이 메일 수신 후 Pay Now 클릭
↓
고객 결제 완료 (PayPal이 자동 처리)
↓
서버에서 상태 조회 또는 Webhook으로 결제 완료 수신
↓
결제 완료 시 DB 업데이트
```
---

### 3.2 Rest API 정리
a. Create draft invoice
```bash
curl -v -X POST https://api-m.sandbox.paypal.com/v2/invoicing/invoices \
-H 'Authorization: Bearer zekwhYgsYYI0zDg0p_Nf5v78VelCfYR0' \
-H 'Content-Type: application/json' \
-H 'Prefer: return=representation' \
-d '{
  "detail": {
    "invoice_number": "#123",
    "reference": "deal-ref",
    "invoice_date": "2018-11-12",
    "currency_code": "USD",
    "note": "Thank you for your business.",
    "term": "No refunds after 30 days.",
    "memo": "This is a long contract",
    "payment_term": {
      "term_type": "NET_10",
      "due_date": "2018-11-22"
    }
  },
  "invoicer": {
    "name": {
      "given_name": "David",
      "surname": "Larusso"
    },
    "address": {
      "address_line_1": "1234 First Street",
      "address_line_2": "337673 Hillside Court",
      "admin_area_2": "Anytown",
      "admin_area_1": "CA",
      "postal_code": "98765",
      "country_code": "US"
    },
    "email_address": "merchant@example.com",
    "phones": [
      {
        "country_code": "001",
        "national_number": "4085551234",
        "phone_type": "MOBILE"
      }
    ],
    "website": "www.test.com",
    "tax_id": "ABcNkWSfb5ICTt73nD3QON1fnnpgNKBy- Jb5SeuGj185MNNw6g",
    "logo_url": "https://example.com/logo.PNG",
    "additional_notes": "2-4"
  },
  "primary_recipients": [
    {
      "billing_info": {
        "name": {
          "given_name": "Stephanie",
          "surname": "Meyers"
        },
        "address": {
          "address_line_1": "1234 Main Street",
          "admin_area_2": "Anytown",
          "admin_area_1": "CA",
          "postal_code": "98765",
          "country_code": "US"
        },
        "email_address": "bill-me@example.com",
        "phones": [
          {
            "country_code": "001",
            "national_number": "4884551234",
            "phone_type": "HOME"
          }
        ],
        "additional_info_value": "add-info"
      },
      "shipping_info": {
        "name": {
          "given_name": "Stephanie",
          "surname": "Meyers"
        },
        "address": {
          "address_line_1": "1234 Main Street",
          "admin_area_2": "Anytown",
          "admin_area_1": "CA",
          "postal_code": "98765",
          "country_code": "US"
        }
      }
    }
  ],
  "items": [
    {
      "name": "Yoga Mat",
      "description": "Elastic mat to practice yoga.",
      "quantity": "1",
      "unit_amount": {
        "currency_code": "USD",
        "value": "50.00"
      },
      "tax": {
        "name": "Sales Tax",
        "percent": "7.25"
      },
      "discount": {
        "percent": "5"
      },
      "unit_of_measure": "QUANTITY"
    }
  ],
  "configuration": {
    "partial_payment": {
      "allow_partial_payment": true,
      "minimum_amount_due": {
        "currency_code": "USD",
        "value": "20.00"
      }
    },
    "allow_tip": true,
    "tax_calculated_after_discount": true,
    "tax_inclusive": false,
    "template_id": "TEMP-19V05281TU309413B"
  },
  "amount": {
    "breakdown": {
      "custom": {
        "label": "Packing Charges",
        "amount": {
          "currency_code": "USD",
          "value": "10.00"
        }
      },
      "shipping": {
        "amount": {
          "currency_code": "USD",
          "value": "10.00"
        },
        "tax": {
          "name": "Sales Tax",
          "percent": "7.25"
        }
      },
      "discount": {
        "invoice_discount": {
          "percent": "5"
        }
      }
    }
  }
}'
```
```json
{
  "id": "INV2-Z56S-5LLA-Q52L-CPZ5",
  "status": "DRAFT",
  "detail": {
    "invoice_number": "#123",
    "reference": "deal-ref",
    "invoice_date": "2018-11-12",
    "currency_code": "USD",
    "note": "Thank you for your business.",
    "term": "No refunds after 30 days.",
    "memo": "This is a long contract",
    "payment_term": {
      "term_type": "NET_10",
      "due_date": "2018-11-22"
    },
    "metadata": {
      "create_time": "2018-11-12T08:00:20Z",
      "recipient_view_url": "https://www.api-m.paypal.com/invoice/p#Z56S5LLAQ52LCPZ5",
      "invoicer_view_url": "https://www.api-m.paypal.com/invoice/details/INV2-Z56S-5LLA-Q52L-CPZ5"
    }
  },
  "invoicer": {
    "name": {
      "given_name": "David",
      "surname": "Larusso"
    },
    "address": {
      "address_line_1": "1234 First Street",
      "address_line_2": "337673 Hillside Court",
      "admin_area_2": "Anytown",
      "admin_area_1": "CA",
      "postal_code": "98765",
      "country_code": "US"
    },
    "email_address": "merchant@example.com",
    "phones": [
      {
        "country_code": "001",
        "national_number": "4085551234",
        "phone_type": "MOBILE"
      }
    ],
    "website": "https://example.com",
    "tax_id": "ABcNkWSfb5ICTt73nD3QON1fnnpgNKBy-Jb5SeuGj185MNNw6g",
    "logo_url": "https://example.com/logo.PNG",
    "additional_notes": "2-4"
  },
  "primary_recipients": [
    {
      "billing_info": {
        "name": {
          "given_name": "Stephanie",
          "surname": "Meyers"
        },
        "address": {
          "address_line_1": "1234 Main Street",
          "admin_area_2": "Anytown",
          "admin_area_1": "CA",
          "postal_code": "98765",
          "country_code": "US"
        },
        "email_address": "bill-me@example.com",
        "phones": [
          {
            "country_code": "001",
            "national_number": "4884551234",
            "phone_type": "HOME"
          }
        ],
        "additional_info_value": "add-info"
      },
      "shipping_info": {
        "name": {
          "given_name": "Stephanie",
          "surname": "Meyers"
        },
        "address": {
          "address_line_1": "1234 Main Street",
          "admin_area_2": "Anytown",
          "admin_area_1": "CA",
          "postal_code": "98765",
          "country_code": "US"
        }
      }
    }
  ],
  "items": [
    {
      "name": "Yoga Mat",
      "description": "Elastic mat to practice yoga.",
      "quantity": "1",
      "unit_amount": {
        "currency_code": "USD",
        "value": "50.00"
      },
      "tax": {
        "name": "Sales Tax",
        "percent": "7.25",
        "amount": {
          "currency_code": "USD",
          "value": "3.27",
          "tax_note": "Reduced tax rate"
        }
      },
      "discount": {
        "percent": "5",
        "amount": {
          "currency_code": "USD",
          "value": "2.5"
        }
      },
      "unit_of_measure": "QUANTITY"
    }
  ],
  "configuration": {
    "partial_payment": {
      "allow_partial_payment": true,
      "minimum_amount_due": {
        "currency_code": "USD",
        "value": "20.00"
      }
    },
    "allow_tip": true,
    "tax_calculated_after_discount": true,
    "tax_inclusive": false,
    "template_id": "TEMP-19V05281TU309413B"
  },
  "amount": {
    "currency_code": "USD",
    "value": "74.21",
    "breakdown": {
      "item_total": {
        "currency_code": "USD",
        "value": "60.00"
      },
      "custom": {
        "label": "Packing Charges",
        "amount": {
          "currency_code": "USD",
          "value": "10.00"
        }
      },
      "shipping": {
        "amount": {
          "currency_code": "USD",
          "value": "10.00"
        },
        "tax": {
          "name": "Sales Tax",
          "percent": "7.25",
          "amount": {
            "currency_code": "USD",
            "value": "0.73",
            "tax_note": "Reduced tax rate"
          }
        }
      },
      "discount": {
        "item_discount": {
          "currency_code": "USD",
          "value": "-7.50"
        },
        "invoice_discount": {
          "percent": "5",
          "amount": {
            "currency_code": "USD",
            "value": "-2.63"
          }
        }
      },
      "tax_total": {
        "currency_code": "USD",
        "value": "4.34"
      }
    }
  },
  "due_amount": {
    "currency_code": "USD",
    "value": "74.21"
  },
  "links": [
    {
      "href": "https://api-m.paypal.com/v2/invoicing/invoices/INV2-Z56S-5LLA-Q52L-CPZ5",
      "rel": "self",
      "method": "GET"
    },
    {
      "href": "https://api-m.paypal.com/v2/invoicing/invoices/INV2-Z56S-5LLA-Q52L-CPZ5/send",
      "rel": "send",
      "method": "POST"
    },
    {
      "href": "https://api-m.paypal.com/v2/invoicing/invoices/INV2-Z56S-5LLA-Q52L-CPZ5/update",
      "rel": "replace",
      "method": "PUT"
    },
    {
      "href": "https://api-m.paypal.com/v2/invoicing/invoices/INV2-Z56S-5LLA-Q52L-CPZ5",
      "rel": "delete",
      "method": "DELETE"
    },
    {
      "href": "https://api-m.paypal.com/v2/invoicing/invoices/INV2-Z56S-5LLA-Q52L-CPZ5/payments",
      "rel": "record-payment",
      "method": "POST"
    },
    {
      "href": "https://api-m.paypal.com/v2/invoicing/invoices/INV2-Z56S-5LLA-Q52L-CPZ5/generate-qr-code",
      "rel": "qr-code",
      "method": "POST"
    }
  ]
}
```
<br>
b. Send invoice

- PayPal Developers에서 Webhook 추가 가능

---
### 3.3 서버 구축

**a. 인보이스 생성**

- 응답값으로 받아온 `invoice_id`, `recipient_view_url`, 연관된 `payments` 객체를 DB에 저장 (`invoice` 테이블 추가).

**b. 인보이스 전송**

- 기본적으로 수신자에게 PayPal이 이메일 자동 발송.

**c. 고객이 결제**

- 고객은 이메일로 받은 링크를 클릭해서 결제.
- 결제 완료시, PayPal이 인보이스를 자동으로 PAID 상태로 업데이트.

**d. 웹 훅 등록**

- Webhook URL (우리 서버)을 등록해두면 PayPal이 결제 완료될 때마다 실시간으로 알림 보내줌.
- 이벤트 : `INVOICING.INVOICE.PAID` (인보이스 결제 완료).
- 결제 완료 웹 훅을 받으면, DB 업데이트 → 우체국 택배 접수.
- 웹 훅은 1번만 등록하면 됨.
    - URL이 바뀌거나 / 이벤트 타입 추가할 때만 재등록 또는 업데이트 필요.
    - PayPal 서버에 저장되어 관리되기 때문에 따로 저장하지 않아도 됨.

---
### 3.3 테스트

- ngrok HTTPS 터널을 생성해서 로컬호스트 주소를 우회한 URL을 웹훅 서버 URL로 등록.
<br>

- Invoice 생성 및 전송 요청을 보낸 후, DB에 Status.DRAFT부터 저장.

<img width="951" alt="image (18)" src="https://github.com/user-attachments/assets/489b4cb0-8e77-4dea-9864-0557ceaaaae8" />
<br>

- 전송된 사용자(결제하는 고객)의 ID로 로그인시 해당 메일 인보이스가 보임.

<img width="1359" alt="image (19)" src="https://github.com/user-attachments/assets/0155af74-d973-4312-8030-ca5e1d75ed42" />
<br>

- 사용자가 결제할 경우, 해당 Invoice의 상태가 PAID로 변경되며 이때 이벤트가 발생하여 웹훅으로 백엔드 서버에서 요청을 응답받을 수 있음.
- 이때, 백엔드 상태도 PAID로 업데이트.

<img width="939" alt="image (20)" src="https://github.com/user-attachments/assets/a602aee6-fcec-44d7-8e91-8a73329a0468" />
