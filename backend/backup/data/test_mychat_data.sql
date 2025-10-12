-- MyChat 테스트용 데이터 INSERT

-- 1. Actor 테이블에 OCEAN 페르소나 ID들 확인/추가 (필요시)
INSERT IGNORE INTO actors (id, kind, user_id, created_at, updated_at) VALUES
(1, 'PERSONA', NULL, NOW(), NOW()),  -- Openness
(2, 'PERSONA', NULL, NOW(), NOW()),  -- Conscientiousness
(3, 'PERSONA', NULL, NOW(), NOW()),  -- Extraversion
(4, 'PERSONA', NULL, NOW(), NOW()),  -- Agreeableness
(5, 'PERSONA', NULL, NOW(), NOW());  -- Neuroticism

-- 2. 테스트용 사용자 메시지들
INSERT INTO my_chat_messages (user_id, sender_actor_id, message, is_read, created_at, updated_at) VALUES
-- 사용자 메시지 (user_id=6이 본인이므로 sender_actor_id도 6, is_read=true)
(6, 6, '오늘 정말 피곤해', true, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
(6, 6, '새로운 프로젝트 시작했는데 걱정돼', true, '2024-01-01 11:00:00', '2024-01-01 11:00:00'),
(6, 6, '친구들이랑 만나서 재밌게 놀았어!', true, '2024-01-01 12:00:00', '2024-01-01 12:00:00'),

-- AI 페르소나 응답들 (안읽음으로 설정)
-- 첫 번째 대화 "오늘 정말 피곤해"에 대한 응답
(6, 5, '아 정말 힘든 하루였구나ㅠㅠ 나도 그런 적 있어... 괜찮아?', false, '2024-01-01 10:01:00', '2024-01-01 10:01:00'),
(6, 1, '오? 뭔가 새로운 일이 있었나? 어떤 상황이었어?', false, '2024-01-01 10:02:00', '2024-01-01 10:02:00'),
(6, 4, '힘들 때는 충분히 쉬는 게 중요해~ 무리하지 마!', false, '2024-01-01 10:03:00', '2024-01-01 10:03:00'),

-- 두 번째 대화 "새로운 프로젝트"에 대한 응답
(6, 5, '새로운 거 시작할 때 걱정되는 건 당연해... 나도 그래ㅠ', false, '2024-01-01 11:01:00', '2024-01-01 11:01:00'),
(6, 2, '차근차근 계획 세워보면 괜찮을 거야! 하나씩 해보자', false, '2024-01-01 11:02:00', '2024-01-01 11:02:00'),
(6, 1, '와! 새로운 프로젝트라니 흥미진진하다~ 어떤 거야?', false, '2024-01-01 11:03:00', '2024-01-01 11:03:00'),

-- 세 번째 대화 "친구들과 놀았어"에 대한 응답
(6, 3, '와! 그거 좋았겠다!! 나도 친구들 만나고 싶어~', false, '2024-01-01 12:01:00', '2024-01-01 12:01:00'),
(6, 4, '친구들과 시간 보내는 게 정말 소중하지~ 기분 좋겠어!', false, '2024-01-01 12:02:00', '2024-01-01 12:02:00'),
(6, 1, '어떤 재밌는 일 했어?? 새로운 경험이었나?', false, '2024-01-01 12:03:00', '2024-01-01 12:03:00');

-- 확인용 쿼리들 (주석으로 참고)
-- SELECT * FROM my_chat_messages WHERE user_id = 6 ORDER BY created_at;
-- SELECT COUNT(*) FROM my_chat_messages WHERE user_id = 6 AND is_read = false;
-- SELECT * FROM actors WHERE id IN (1,2,3,4,5,6);