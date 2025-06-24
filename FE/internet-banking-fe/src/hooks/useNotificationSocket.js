import { useEffect } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useDispatch, useSelector } from "react-redux";
import { fetchNotifications } from "../redux/notificationsSlice";

export default function useNotificationSocket() {
  const dispatch = useDispatch();
  const userId = useSelector((state) => state.user.userId);

  useEffect(() => {
    if (!userId) return;

    const socketUrl = `${import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"}/ws-notifications`;
    const socket = new SockJS(socketUrl);
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log("WebSocket connected");
        client.subscribe(`/topic/notifications/${userId}`, (message) => {
          console.log("New notification received:", message);
          // Force re-fetch notifications
          dispatch(fetchNotifications());
        });
      },
      onDisconnect: () => {
        console.log("WebSocket disconnected");
      },
      onStompError: (frame) => {
        console.error("WebSocket error:", frame);
      }
    });

    client.activate();

    return () => {
      if (client.active) {
        client.deactivate();
      }
    };
  }, [userId, dispatch]);
}