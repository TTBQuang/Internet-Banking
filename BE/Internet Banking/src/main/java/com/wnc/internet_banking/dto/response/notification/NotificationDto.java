package com.wnc.internet_banking.dto.response.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Thông báo được gửi đến người dùng")
@Getter
public class NotificationDto {
    @Schema(description = "ID thông báo")
    private UUID notificationId;

    @Schema(description = "Tiêu đề thông báo")
    private String title;

    @Schema(description = "Nội dung thông báo")
    private String content;

    @Schema(description = "Thời điểm tạo thông báo")
    private LocalDateTime createdAt;

    @Schema(description = "Trạng thái đã đọc hay chưa")
    private boolean isRead;
}


