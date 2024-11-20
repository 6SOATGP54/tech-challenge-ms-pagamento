package com.tech_challenge.ms_pagamento.dtos;

public record WebhookPayloadDTO(String action,
                                String api_version,
                                Data data,
                                String date_created,
                                Long id,
                                boolean live_mode,
                                String type,
                                String user_id) {

    public record Data(String id) {}

}
