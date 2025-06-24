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
      onConnect: () => {
        client.subscribe(`/topic/notifications/${userId}`, () => {
          console.log("receive notifications");
          dispatch(fetchNotifications());
        });
      },
    });

    client.activate();

    return () => {
      console.log("userId in useNotificationSocket", userId);
    if (!userId) return;
      client.deactivate();
    };
  }, [userId, dispatch]);
} 