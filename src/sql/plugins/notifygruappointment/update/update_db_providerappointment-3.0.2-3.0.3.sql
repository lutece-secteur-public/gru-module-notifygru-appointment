DROP PROCEDURE IF EXISTS UpdateMessages;

DELIMITER //

CREATE PROCEDURE UpdateMessages()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE entry_id INT;
    DECLARE position_val INT;

    DECLARE cur CURSOR FOR
        SELECT entry.id_entry, entry.pos
        FROM workflow_task_notify_gru_cf cf
        JOIN genatt_entry entry ON cf.id_spring_provider LIKE CONCAT('%', entry.id_resource)
        WHERE cf.message_guichet LIKE '%${reponse_%'
           OR cf.message_agent LIKE '%${reponse_%'
           OR cf.message_email LIKE '%${reponse_%'
           OR cf.message_sms LIKE '%${reponse_%'
           OR cf.message_broadcast LIKE '%${reponse_%';

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO entry_id, position_val;

        IF done THEN
            LEAVE read_loop;
        END IF;

        UPDATE workflow_task_notify_gru_cf cf
        JOIN genatt_entry entry ON cf.id_spring_provider LIKE CONCAT('%', entry.id_resource)
        SET cf.message_guichet = IF(cf.message_guichet IS NOT NULL AND cf.message_guichet LIKE '%${reponse_%',
            REPLACE(cf.message_guichet, CONCAT('${reponse_', position_val, '!}'), CONCAT('${reponse_', entry_id, '!}')),
            cf.message_guichet),
            cf.message_agent = IF(cf.message_agent IS NOT NULL AND cf.message_agent LIKE '%${reponse_%',
            REPLACE(cf.message_agent, CONCAT('${reponse_', position_val, '!}'), CONCAT('${reponse_', entry_id, '!}')),
            cf.message_agent),
            cf.message_email = IF(cf.message_email IS NOT NULL AND cf.message_email LIKE '%${reponse_%',
            REPLACE(cf.message_email, CONCAT('${reponse_', position_val, '!}'), CONCAT('${reponse_', entry_id, '!}')),
            cf.message_email),
            cf.message_sms = IF(cf.message_sms IS NOT NULL AND cf.message_sms LIKE '%${reponse_%',
            REPLACE(cf.message_sms, CONCAT('${reponse_', position_val, '!}'), CONCAT('${reponse_', entry_id, '!}')),
            cf.message_sms),
            cf.message_broadcast = IF(cf.message_broadcast IS NOT NULL AND cf.message_broadcast LIKE '%${reponse_%',
            REPLACE(cf.message_broadcast, CONCAT('${reponse_', position_val, '!}'), CONCAT('${reponse_', entry_id, '!}')),
            cf.message_broadcast)
        WHERE entry.id_entry = entry_id
          AND entry.pos = position_val;
    END LOOP;

    CLOSE cur;
END //

DELIMITER ;

CALL UpdateMessages();