CREATE TABLE IF NOT EXISTS notification_data.notification (
                                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                         user_id VARCHAR(255) REFERENCES user_data."user"(keycloak_id) ON DELETE CASCADE,
                                                         target_user_id VARCHAR(255) REFERENCES user_data."user"(keycloak_id) ON DELETE CASCADE NOT NULL,
                                                         entity_id UUID,
                                                         entity_type VARCHAR(50) NOT NULL,
                                                         content TEXT NOT NULL,
                                                         notification_type VARCHAR(50) NOT NULL ,
                                                         is_read BOOLEAN DEFAULT FALSE,
                                                         created_at TIMESTAMP DEFAULT NOW());