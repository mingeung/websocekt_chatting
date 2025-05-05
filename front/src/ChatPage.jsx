import { useEffect, useRef, useState, useCallback } from "react";
import * as StompJs from "@stomp/stompjs";
import axios from "axios";

export default function ChatPage() {
  const client = useRef(null);
  const [message, setMessage] = useState(""); // 입력창 값 저장
  const [chatMessages, setChatMessages] = useState([]); // 전체 채팅 메시지
  const chatRoomId = 1; // 현재 채팅방 ID
  const [page, setPage] = useState(0); // 페이지 상태
  const [hasMore, setHasMore] = useState(true); // 더 불러올 메시지가 있는지 여부
  const chatContainerRef = useRef(null); // 채팅창 ref
  const hasFetchedRef = useRef(false);

  // ✅ 메시지 페이징 불러오기
  const fetchMessages = async (pageNum) => {
    try {
      const res = await axios.get(
        `http://localhost:8080/${chatRoomId}/messages/paged?page=${pageNum}`
      );

      const messages = res?.data?.content?.map((msg) => msg.content);

      if (messages?.length === 0) {
        setHasMore(false);
        return;
      }

      // 현재 스크롤 위치 저장
      const container = chatContainerRef?.current;
      const scrollHeightBefore = container?.scrollHeight;

      // 기존 메시지 앞에 추가 (prepend)
      setChatMessages((prev) => [...prev, ...messages]);
    } catch (err) {
      console.error("메시지 로딩 실패", err);
    }
  };

  // 소켓 연결
  const connect = () => {
    try {
      client.current = new StompJs.Client({
        brokerURL: "ws://172.16.100.242:8080/stomp/chat",
        connectHeaders: {},
        debug: function (str) {
          console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          client.current?.subscribe(`/sub/chat/1`, (message) => {
            setChatMessages((prev) => [message?.body, ...prev]);
          });
          console.log("연결성공");
        },
        onStompError: (frame) => {
          console.error("Broker reported error: " + frame.headers["message"]);
          console.error("Additional details: " + frame.body);
        },
      });

      if (client.current) {
        client.current.activate();
      } else {
        console.log("클라이언트가 초기화되지 않았습니다.");
      }
    } catch (err) {
      console.log(err);
    }
  };

  function handleButtomClick() {
    console.log("내가 적은 메시지:", message);
    if (!message.trim()) return; // 빈 메시지 방지

    client.current?.publish({
      destination: `/pub/chatMessage/1`,
      body: JSON.stringify({
        content: message,
      }),
    });
    setMessage(""); // 전송 후 입력창 초기화
  }

  // ✅ IntersectionObserver 설정
  const observer = useRef(null);

  const observeTop = useCallback(
    (node) => {
      if (observer.current) observer.current.disconnect();
      observer.current = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting && hasMore) {
            console.log("상단 도달!");
            setPage((prev) => prev + 1);
          }
        },
        { root: chatContainerRef.current, threshold: 1.0 }
      );
      if (node) observer.current.observe(node);
    },
    [hasMore]
  );

  useEffect(() => {
    connect();
  }, []);

  useEffect(() => {
    if (!hasFetchedRef.current && page === 0) {
      hasFetchedRef.current = true;
      console.log("✅ 첫 페이지 불러오기 실행");
      fetchMessages(0);
    } else if (page !== 0 && hasMore) {
      fetchMessages(page);
    }

    console.log("page:", page);
  }, [page]);

  return (
    <div style={{ padding: "20px", maxWidth: "500px", margin: "0 auto" }}>
      <h2>채팅방 #{chatRoomId}</h2>

      <div
        ref={chatContainerRef}
        style={{
          border: "1px solid #ccc",
          height: "300px",
          overflowY: "auto",
          padding: "10px",
          marginBottom: "10px",
          backgroundColor: "#f9f9f9",
          display: "flex",
          flexDirection: "column-reverse", // 메시지가 아래부터 쌓이게
        }}
      >
        {chatMessages.map((msg, idx) => (
          <div key={idx} style={{ marginBottom: "8px" }}>
            <span>{msg}</span>
          </div>
        ))}
        <div ref={observeTop} style={{ height: "1px" }} />
      </div>

      <div style={{ display: "flex", gap: "10px" }}>
        <input
          type="text"
          placeholder="메시지를 입력하세요"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          style={{ flex: 1, padding: "8px" }}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              handleButtomClick();
            }
          }}
        />
        <button onClick={handleButtomClick}>전송</button>
      </div>
    </div>
  );
}
