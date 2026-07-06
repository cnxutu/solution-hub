# video-osd-simulator-v1 前端 WS 解密对接说明

本文档面向前端联调人员，说明如何对 `video-osd-simulator-v1` 推送的 WebSocket 密文进行解密，并还原出原始 OSD JSON。

## 1. 对接时后端提供给前端的信息

当前联调所需固定信息如下：

```text
algorithm: AES-GCM
kid: relay-key-01
key-base64: MDEyMzQ1Njc4OWFiY2RlZg==
ws: ws://<host>:<port>/ws/osd
```

说明：

- `kid` 是密钥标识，用来告诉前端该使用哪一把密钥
- `key-base64` 是 Base64 编码后的 AES 密钥
- 前端需要先把 `key-base64` 解码成原始字节，再参与解密

## 2. WebSocket 收到的密文结构

前端从 `/ws/osd` 收到的数据不是直接业务 JSON，而是一个密文 envelope，例如：

```json
{
  "encrypted": true,
  "alg": "AES-GCM",
  "kid": "relay-key-01",
  "iv": "6bQxEQjFFiVe9AHy",
  "ciphertext": "IqGyauC43Cg8DrAm6DGPSNY5CuKujBggIZh8ehuAscGjwnqeiYHwclC8Azl6TU/YxBuWY1ZdgcEppG8AhVgIXo3pX1srlUEJcqj9PMJttZi0e8agCwKKEO29L+DYq5nATLtN87+ZreIvKPZtqcykmGYTgvC14nCj54/66oQuduHDRexAxklqZAxbFYLMI2PAOTCF1O5EEFerdN6SPP+KhIOPBoY8x6q1ui53rR818+7aMct0tRBwuqyatJdwOiE0wH96ELZh7om0NTEIPD4rDYuZQwGM7YwXzBoH9eRz1clHhLMqtVpra9jy4Gu3mlujXbHZfocitm6ukbjR+NyfMpD2+tsH6GJQF11VYYFixrRLWasjMhG0s/Z9kJr0kOf4xHmIifK+u+w/HNae3F/6tNQw45LxZAtVvQua35ns2nCBE9NERJFVLFQZySsZhBeLu7GVBp5qh4s+Drv9lWJeZlIExMWaYCwGi63Ni6VG372TZSfOVKvd++PpFssbRjFkKMzuj/HScxa4F+/9jvbt+wt+sLGcrTQfyJRP9ErYgIi/lgaEMhhDmcBKyZgspgdj7rAbp7fvAT+qnxQ01dhvnFcrlHDnF5DtiY18DQUCwBU2s/1zpG0EK9TrxqjrMuhRie0y2/6RvHfB+nZzTCP84OjyPXJKG3Ue+ijf2/yKBgd4TJjjuuHOOdBTZmucoZwEuE8m"
}
```

字段说明：

| 字段 | 说明 |
| --- | --- |
| `encrypted` | 是否为密文消息 |
| `alg` | 加密算法，当前固定为 `AES-GCM` |
| `kid` | 密钥标识 |
| `iv` | AES-GCM 使用的初始化向量，Base64 编码 |
| `ciphertext` | 密文内容，Base64 编码 |

## 3. 解密核心逻辑

前端不能只靠 `iv + ciphertext` 解密，必须同时持有正确的密钥。

完整流程如下：

1. 读取 `kid`
2. 根据 `kid` 找到对应的 `key-base64`
3. 将 `key-base64` 做 Base64 解码，得到 AES 密钥字节
4. 将 `iv` 做 Base64 解码
5. 将 `ciphertext` 做 Base64 解码
6. 使用 `AES-GCM` 算法进行解密
7. 得到原始 JSON 字符串
8. 再对原始 JSON 做 `JSON.parse`

## 4. 当前这条真实数据的解密结果

下面这条真实密文：

```text
2026-07-06T10:01:54.119Z  gap=0ms
{"encrypted":true,"alg":"AES-GCM","kid":"relay-key-01","iv":"6bQxEQjFFiVe9AHy","ciphertext":"IqGyauC43Cg8DrAm6DGPSNY5CuKujBggIZh8ehuAscGjwnqeiYHwclC8Azl6TU/YxBuWY1ZdgcEppG8AhVgIXo3pX1srlUEJcqj9PMJttZi0e8agCwKKEO29L+DYq5nATLtN87+ZreIvKPZtqcykmGYTgvC14nCj54/66oQuduHDRexAxklqZAxbFYLMI2PAOTCF1O5EEFerdN6SPP+KhIOPBoY8x6q1ui53rR818+7aMct0tRBwuqyatJdwOiE0wH96ELZh7om0NTEIPD4rDYuZQwGM7YwXzBoH9eRz1clHhLMqtVpra9jy4Gu3mlujXbHZfocitm6ukbjR+NyfMpD2+tsH6GJQF11VYYFixrRLWasjMhG0s/Z9kJr0kOf4xHmIifK+u+w/HNae3F/6tNQw45LxZAtVvQua35ns2nCBE9NERJFVLFQZySsZhBeLu7GVBp5qh4s+Drv9lWJeZlIExMWaYCwGi63Ni6VG372TZSfOVKvd++PpFssbRjFkKMzuj/HScxa4F+/9jvbt+wt+sLGcrTQfyJRP9ErYgIi/lgaEMhhDmcBKyZgspgdj7rAbp7fvAT+qnxQ01dhvnFcrlHDnF5DtiY18DQUCwBU2s/1zpG0EK9TrxqjrMuhRie0y2/6RvHfB+nZzTCP84OjyPXJKG3Ue+ijf2/yKBgd4TJjjuuHOOdBTZmucoZwEuE8m"}
```

使用下面这组配置：

```text
kid: relay-key-01
key-base64: MDEyMzQ1Njc4OWFiY2RlZg==
```

可以实际解密得到：

```json
{
  "timestamp": 1782972590947,
  "latitude": 30.18566738053386,
  "longitude": 120.19797559348879,
  "height": 100.24636,
  "corners": [
    {
      "lat": 30.18516532898232,
      "lon": 120.1975654535184
    },
    {
      "lat": 30.18620373977289,
      "lon": 120.1976284113682
    },
    {
      "lat": 30.1861694320854,
      "lon": 120.1983857334591
    },
    {
      "lat": 30.18513102129483,
      "lon": 120.1983227756093
    }
  ],
  "attitude_head": 87.0,
  "speed_x": 0.0,
  "speed_y": 0.0,
  "speed_z": 1.0,
  "gimbal_pitch": -0.1,
  "gimbal_roll": 1.5,
  "gimbal_yaw": 87.3602828699535,
  "frame_center": {
    "lat": 30.18566738053386,
    "lon": 120.19797559348879
  }
}
```

这说明当前后端推送的密文格式和密钥配置是可正常解密的。

## 5. 前端示例代码

以下示例基于浏览器原生 `Web Crypto API`。

```javascript
const keyMap = {
  "relay-key-01": "MDEyMzQ1Njc4OWFiY2RlZg=="
};

function base64ToUint8Array(base64) {
  const binary = atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i += 1) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes;
}

async function decryptWsEnvelope(envelope) {
  const keyBase64 = keyMap[envelope.kid];
  if (!keyBase64) {
    throw new Error(`未找到 kid=${envelope.kid} 对应的密钥`);
  }

  const rawKey = base64ToUint8Array(keyBase64);
  const iv = base64ToUint8Array(envelope.iv);
  const ciphertext = base64ToUint8Array(envelope.ciphertext);

  const cryptoKey = await crypto.subtle.importKey(
    "raw",
    rawKey,
    { name: "AES-GCM" },
    false,
    ["decrypt"]
  );

  const plainBuffer = await crypto.subtle.decrypt(
    { name: "AES-GCM", iv },
    cryptoKey,
    ciphertext
  );

  const plainText = new TextDecoder().decode(plainBuffer);
  return JSON.parse(plainText);
}

const socket = new WebSocket("ws://127.0.0.1:18083/ws/osd");

socket.onmessage = async (event) => {
  try {
    const envelope = JSON.parse(event.data);

    if (envelope.encrypted !== true || envelope.alg !== "AES-GCM") {
      console.warn("收到的不是 AES-GCM 密文消息", envelope);
      return;
    }

    const osd = await decryptWsEnvelope(envelope);
    console.log("解密后的 OSD 数据:", osd);
  } catch (error) {
    console.error("WS 解密失败:", error);
  }
};
```

## 6. 前端联调排查建议

如果前端仍然解不开，可按下面顺序检查：

1. `kid` 是否一致  
   当前应为：`relay-key-01`

2. `key-base64` 是否一致  
   当前应为：`MDEyMzQ1Njc4OWFiY2RlZg==`

3. 是否先做了 Base64 解码  
   `key`、`iv`、`ciphertext` 都需要先 Base64 解码

4. 是否使用了 `AES-GCM`  
   不是 `AES-CBC`，也不是自定义 XOR

5. 浏览器是否使用了 `crypto.subtle.decrypt`

6. 是否把解密结果再次做了 `JSON.parse`

## 7. 关键结论

前端真正需要记住的只有一句话：

`拿 kid 找 key，把 key / iv / ciphertext 全部做 Base64 解码，再用 AES-GCM 解密，最后 JSON.parse。`
