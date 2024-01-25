UPDATE workflow_task_notify_gru_cf AS cf
JOIN (
    SELECT
        cf.id_task,
        GROUP_CONCAT(ge.id_entry ORDER BY ge.pos) AS grouped_id_entries,
        cf.message_guichet,
        cf.message_agent,
        cf.message_email,
        cf.message_sms,
        cf.message_broadcast
    FROM workflow_task_notify_gru_cf AS cf
    JOIN genatt_entry AS ge
        ON cf.id_spring_provider LIKE CONCAT('%', ge.id_resource)
     WHERE 
        (
            (cf.message_guichet IS NOT NULL AND cf.message_guichet LIKE '%${reponse_%')
            OR (cf.message_agent IS NOT NULL AND cf.message_agent LIKE '%${reponse_%')
            OR (cf.message_email IS NOT NULL AND cf.message_email LIKE '%${reponse_%')
            OR (cf.message_sms IS NOT NULL AND cf.message_sms LIKE '%${reponse_%')
            OR (cf.message_broadcast IS NOT NULL AND cf.message_broadcast LIKE '%${reponse_%')
        )
    GROUP BY cf.id_task
) AS grouped_entries ON cf.id_task = grouped_entries.id_task
SET
cf.message_guichet =
	IF(cf.message_guichet IS NOT NULL AND cf.message_guichet LIKE '%${reponse_%',
		 REPLACE(
                REPLACE(
                    REPLACE(
                        REPLACE(
                            REPLACE(
                                REPLACE(
                                    REPLACE(
                                        REPLACE(
                                            REPLACE(
                                                REPLACE(
                                                    grouped_entries.message_guichet,
                                                    '${reponse_0!}',
                                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 1), ',', -1), '}')
                                                ),
                                                '${reponse_1!}',
                                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 2), ',', -1), '}')
                                            ),
                                            '${reponse_2!}',
                                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 3), ',', -1), '}')
                                        ),
                                        '${reponse_3!}',
                                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 4), ',', -1), '}')
                                    ),
                                    '${reponse_4!}',
                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 5), ',', -1), '}')
                                ),
                                '${reponse_5!}',
                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 6), ',', -1), '}')
                            ),
                            '${reponse_6!}',
                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 7), ',', -1), '}')
                        ),
                        '${reponse_7!}',
                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 8), ',', -1), '}')
                    ),
                    '${reponse_8!}',
                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 9), ',', -1), '}')
                ),
                '${reponse_9!}',
                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 10), ',', -1), '}')
            ),
	cf.message_guichet),

cf.message_agent =
	IF(cf.message_agent IS NOT NULL AND cf.message_agent LIKE '%${reponse_%',
		REPLACE(
                REPLACE(
                    REPLACE(
                        REPLACE(
                            REPLACE(
                                REPLACE(
                                    REPLACE(
                                        REPLACE(
                                            REPLACE(
                                                REPLACE(
                                                    grouped_entries.message_agent,
                                                    '${reponse_0!}',
                                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 1), ',', -1), '}')
                                                ),
                                                '${reponse_1!}',
                                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 2), ',', -1), '}')
                                            ),
                                            '${reponse_2!}',
                                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 3), ',', -1), '}')
                                        ),
                                        '${reponse_3!}',
                                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 4), ',', -1), '}')
                                    ),
                                    '${reponse_4!}',
                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 5), ',', -1), '}')
                                ),
                                '${reponse_5!}',
                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 6), ',', -1), '}')
                            ),
                            '${reponse_6!}',
                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 7), ',', -1), '}')
                        ),
                        '${reponse_7!}',
                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 8), ',', -1), '}')
                    ),
                    '${reponse_8!}',
                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 9), ',', -1), '}')
                ),
                '${reponse_9!}',
                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 10), ',', -1), '}')
            ),
	cf.message_agent),

cf.message_email =
	IF(cf.message_email IS NOT NULL AND cf.message_email LIKE '%${reponse_%',
		REPLACE(
                REPLACE(
                    REPLACE(
                        REPLACE(
                            REPLACE(
                                REPLACE(
                                    REPLACE(
                                        REPLACE(
                                            REPLACE(
                                                REPLACE(
                                                    grouped_entries.message_email,
                                                    '${reponse_0!}',
                                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 1), ',', -1), '}')
                                                ),
                                                '${reponse_1!}',
                                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 2), ',', -1), '}')
                                            ),
                                            '${reponse_2!}',
                                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 3), ',', -1), '}')
                                        ),
                                        '${reponse_3!}',
                                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 4), ',', -1), '}')
                                    ),
                                    '${reponse_4!}',
                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 5), ',', -1), '}')
                                ),
                                '${reponse_5!}',
                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 6), ',', -1), '}')
                            ),
                            '${reponse_6!}',
                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 7), ',', -1), '}')
                        ),
                        '${reponse_7!}',
                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 8), ',', -1), '}')
                    ),
                    '${reponse_8!}',
                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 9), ',', -1), '}')
                ),
                '${reponse_9!}',
                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 10), ',', -1), '}')
            ),
	cf.message_email),

cf.message_sms =
	IF(cf.message_sms IS NOT NULL AND cf.message_sms LIKE '%${reponse_%',
		REPLACE(
                REPLACE(
                    REPLACE(
                        REPLACE(
                            REPLACE(
                                REPLACE(
                                    REPLACE(
                                        REPLACE(
                                            REPLACE(
                                                REPLACE(
                                                    grouped_entries.message_sms,
                                                    '${reponse_0!}',
                                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 1), ',', -1), '}')
                                                ),
                                                '${reponse_1!}',
                                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 2), ',', -1), '}')
                                            ),
                                            '${reponse_2!}',
                                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 3), ',', -1), '}')
                                        ),
                                        '${reponse_3!}',
                                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 4), ',', -1), '}')
                                    ),
                                    '${reponse_4!}',
                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 5), ',', -1), '}')
                                ),
                                '${reponse_5!}',
                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 6), ',', -1), '}')
                            ),
                            '${reponse_6!}',
                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 7), ',', -1), '}')
                        ),
                        '${reponse_7!}',
                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 8), ',', -1), '}')
                    ),
                    '${reponse_8!}',
                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 9), ',', -1), '}')
                ),
                '${reponse_9!}',
                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 10), ',', -1), '}')
            ),
	cf.message_sms),

cf.message_broadcast =
	IF(cf.message_broadcast IS NOT NULL AND cf.message_broadcast LIKE '%${reponse_%',
		REPLACE(
                REPLACE(
                    REPLACE(
                        REPLACE(
                            REPLACE(
                                REPLACE(
                                    REPLACE(
                                        REPLACE(
                                            REPLACE(
                                                REPLACE(
                                                    grouped_entries.message_broadcast,
                                                    '${reponse_0!}',
                                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 1), ',', -1), '}')
                                                ),
                                                '${reponse_1!}',
                                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 2), ',', -1), '}')
                                            ),
                                            '${reponse_2!}',
                                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 3), ',', -1), '}')
                                        ),
                                        '${reponse_3!}',
                                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 4), ',', -1), '}')
                                    ),
                                    '${reponse_4!}',
                                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 5), ',', -1), '}')
                                ),
                                '${reponse_5!}',
                                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 6), ',', -1), '}')
                            ),
                            '${reponse_6!}',
                            CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 7), ',', -1), '}')
                        ),
                        '${reponse_7!}',
                        CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 8), ',', -1), '}')
                    ),
                    '${reponse_8!}',
                    CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 9), ',', -1), '}')
                ),
                '${reponse_9!}',
                CONCAT('${reponse_', SUBSTRING_INDEX(SUBSTRING_INDEX(grouped_entries.grouped_id_entries, ',', 10), ',', -1), '}')
            ),
	cf.message_broadcast);