-- Clear existing data (in reverse order of dependencies)
TRUNCATE TABLE diary_chat_messages CASCADE;
TRUNCATE TABLE friend_chat_messages CASCADE;
TRUNCATE TABLE my_chat_messages CASCADE;
TRUNCATE TABLE diaries CASCADE;
TRUNCATE TABLE friend_invitations CASCADE;
TRUNCATE TABLE friends CASCADE;
TRUNCATE TABLE game_bart_clicks CASCADE;
TRUNCATE TABLE game_bart_responses CASCADE;
TRUNCATE TABLE game_bart_results CASCADE;
TRUNCATE TABLE game_gng_responses CASCADE;
TRUNCATE TABLE game_gng_results CASCADE;
TRUNCATE TABLE game_ug_responses CASCADE;
TRUNCATE TABLE game_ug_results CASCADE;
TRUNCATE TABLE game_session_results CASCADE;
TRUNCATE TABLE game_sessions CASCADE;
TRUNCATE TABLE survey_responses_log CASCADE;
TRUNCATE TABLE survey_responses CASCADE;
TRUNCATE TABLE reports CASCADE;
TRUNCATE TABLE user_personas CASCADE;
TRUNCATE TABLE actors CASCADE;
TRUNCATE TABLE users CASCADE;

-- Reset sequences
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE actors_id_seq RESTART WITH 1;
ALTER SEQUENCE user_personas_id_seq RESTART WITH 1;
ALTER SEQUENCE diaries_id_seq RESTART WITH 1;
ALTER SEQUENCE friends_id_seq RESTART WITH 1;
ALTER SEQUENCE friend_invitations_id_seq RESTART WITH 1;

-- Insert test users
INSERT INTO users (email, nickname, provider, social_id, profile_image_url, ai_status) VALUES
('test1@example.com', '바다사랑', 'GOOGLE', 'google_123456', 'https://example.com/avatar1.jpg', 'READY'),
('test2@example.com', '파도타기', 'KAKAO', 'kakao_789012', NULL, 'UNSET'),
('test3@example.com', '깊은바다', 'NAVER', 'naver_345678', 'https://example.com/avatar3.jpg', 'GENERATING'),
('test4@example.com', '산호초', 'GOOGLE', 'google_999999', NULL, 'PLANNED'),
('test5@example.com', '돌고래', 'KAKAO', 'kakao_888888', 'https://example.com/avatar5.jpg', 'READY');

-- Insert actors for users
INSERT INTO actors (kind, user_id, persona_id)
SELECT 'USER', id, NULL FROM users;

-- Insert user personas (페르소나 생성 조건: 설문 완료 + 모든 게임 완료)
-- User 1: 설문 완료 + 모든 게임 완료 → 페르소나 생성 가능
INSERT INTO user_personas (user_id, persona_code, nickname) VALUES
(1, 'O', '호기심이'),
(1, 'C', '성실이'),
(1, 'E', '활발이'),
-- User 2: 설문 완료 + 모든 게임 완료 → 페르소나 생성 가능
(2, 'E', '친화력'),
(2, 'A', '배려심');

-- User 3: 설문 미완료 → 페르소나 생성 불가
-- User 4: 설문 미시작, 게임 진행 중 → 페르소나 생성 불가
-- User 5: 설문 미시작, 게임 중단 → 페르소나 생성 불가

-- Insert actors for personas
INSERT INTO actors (kind, user_id, persona_id)
SELECT 'PERSONA', NULL, id FROM user_personas;

-- Insert diaries
INSERT INTO diaries (user_id, title, content, diary_date) VALUES
(1, '오늘의 감정 일기', '오늘은 정말 기분 좋은 하루였다. 아침부터 날씨가 너무 좋아서 기분이 상쾌했고, 오랜만에 친구들과 함께 바다에 갔다왔다. 파도 소리를 들으며 모래사장을 걸었는데, 발가락 사이로 들어오는 모래의 감촉이 어릴 적 추억을 떠올리게 했다. 친구들과 함께 웃고 떠들며 보낸 시간이 정말 소중하게 느껴졌다. 저녁에는 함께 해산물을 먹으며 오늘 있었던 재미있는 일들을 이야기했다. 이런 순간들이 쌓여서 행복한 삶이 되는 것 같다. 내일도 오늘처럼 감사한 마음으로 하루를 시작해야겠다.', '2025-01-20'),
(1, '새로운 도전', '드디어 새로운 프로젝트를 시작했다. 몇 달 동안 준비하고 고민했던 일인데 막상 시작하니 설레면서도 걱정이 된다. 오늘 첫 회의를 했는데 팀원들이 모두 열정적이어서 든든했다. 각자의 역할을 분담하고 앞으로의 계획을 세웠다. 내가 맡은 부분이 꽤 중요한 역할이라 부담도 되지만, 이것도 성장의 기회라고 생각하니 오히려 동기부여가 된다. 예전의 나였다면 이런 도전을 피했을 텐데, 지금은 불확실함 속에서도 한 발 내딛는 용기를 낼 수 있게 되었다. 앞으로 힘든 순간도 있겠지만 초심을 잃지 않고 끝까지 해내고 싶다. 이 프로젝트를 통해 한 단계 더 성장한 나를 만날 수 있기를 기대한다.', '2025-01-21'),
(1, '휴식의 시간', '오랜만에 아무것도 하지 않고 온전히 쉬는 하루를 보냈다. 평소에는 항상 무언가를 해야 한다는 생각에 쫓기듯 살았는데, 오늘은 그런 모든 것들을 내려놓았다. 아침 늦게 일어나서 좋아하는 커피를 천천히 내려 마시며 창밖을 바라봤다. 하늘에 떠가는 구름을 보면서 멍하니 있었는데, 이런 여유로운 시간이 얼마만인지 모르겠다. 오후에는 소파에 누워 그동안 미뤄뒀던 음악을 들었다. 좋아하는 노래를 들으니 마음이 편안해졌다. 저녁에는 간단한 요리를 해먹고 일찍 잠자리에 들었다. 가끔은 이렇게 아무것도 하지 않는 것이 오히려 많은 것을 하는 것보다 가치있다는 생각이 든다. 내일부터는 다시 바쁜 일상으로 돌아가겠지만, 오늘의 이 여유로움을 기억하며 살아가야겠다.', '2025-01-22'),
(2, '감사한 하루', '오늘 지하철에서 정말 따뜻한 장면을 목격했다. 한 할머니께서 무거운 짐을 들고 계단을 오르시는데, 젊은 청년이 다가가서 짐을 들어드리고 천천히 부축해드렸다. 할머니의 감사하다는 말씀에 청년은 수줍게 웃으며 괜찮다고 했다. 그 모습을 보면서 마음이 따뜻해졌고, 세상은 아직 살만한 곳이라는 생각이 들었다. 회사에서도 동료가 내가 힘들어하는 것을 보고 자기 일이 아닌데도 도와주었다. 평소에는 당연하게 지나쳤을 수도 있는 작은 친절들이 오늘따라 더 크게 다가왔다. 저녁에 집에 오면서 나도 누군가에게 그런 사람이 되어야겠다고 다짐했다. 받은 친절을 다시 베풀고, 따뜻함을 전파하는 사람이 되고 싶다. 오늘 하루 만난 모든 사람들에게 감사한 마음이 든다.', '2025-01-20'),
(2, '운동 시작', '오늘부터 본격적으로 운동을 시작했다. 사실 새해 계획으로 세웠던 것인데 이제야 실행에 옮기게 되었다. 아침 6시에 알람을 맞춰놓고 일어났는데, 평소보다 일찍 일어나니 하루가 길게 느껴졌다. 동네 공원에서 가벼운 조깅으로 시작했다. 처음에는 5분만 뛰어도 숨이 차서 힘들었지만, 천천히 페이스를 조절하니 20분 정도는 뛸 수 있었다. 운동을 마치고 나니 온몸에 땀이 났지만 기분은 상쾌했다. 샤워를 하고 나서 먹은 아침밥이 유독 맛있게 느껴졌다. 오후에는 유튜브를 보면서 홈트레이닝도 따라해봤다. 아직은 동작이 어색하고 힘들지만, 꾸준히 하다보면 익숙해질 것 같다. 운동 일지를 작성하기 시작했는데, 매일 기록하면서 발전하는 모습을 확인하고 싶다. 건강한 몸에 건강한 정신이 깃든다는 말을 믿으며, 이번에는 꼭 꾸준히 해보려고 한다.', '2025-01-23'),
(3, '책 읽기', '오랜만에 서점에 들러 책을 한 권 샀다. 평소 읽고 싶었던 에세이였는데, 제목부터 마음에 들어서 망설임 없이 골랐다. 집에 와서 따뜻한 차를 한 잔 내리고 조용히 책을 펼쳤다. 첫 장부터 작가의 따뜻한 문체에 마음이 편안해졌다. 일상의 소소한 이야기들이 담겨있었는데, 평범한 이야기 속에서 깊은 울림을 느낄 수 있었다. 특히 "행복은 거창한 것이 아니라 매일의 작은 순간들 속에 있다"는 구절이 마음에 남았다. 책을 읽으면서 내 삶도 돌아보게 되었다. 그동안 너무 바쁘게만 살았던 것 같다. 책 속의 주인공처럼 나도 일상 속에서 작은 행복을 찾아보려고 한다. 오늘 저녁 노을이 예뻤던 것, 점심에 먹은 김치찌개가 맛있었던 것, 이런 소소한 것들이 모여 나의 하루를 채워간다는 것을 새삼 느꼈다. 앞으로도 독서하는 시간을 꾸준히 가져야겠다.', '2025-01-19');

-- Insert diary chat messages (페르소나들의 일기 분석)
INSERT INTO diary_chat_messages (diary_id, sender_actor_id, message) VALUES
-- 일기 1: 오늘의 감정 일기 (user_id=1의 페르소나들이 분석)
(1, 1, '오늘 정말 행복한 하루를 보낸 것 같네요. 바다와 친구들, 그리고 맛있는 음식까지 완벽한 조합이었어요.'),
(1, 7, '친구들과의 시간을 소중하게 여기는 모습이 보여서 좋아요. 이런 경험들이 당신의 개방성을 더욱 풍부하게 만들어줄 거예요.'),
(1, 1, '어릴 적 추억을 떠올리며 느낀 그 감정, 정말 소중한 순간이었을 것 같아요. 과거와 현재를 연결하는 아름다운 경험이네요.'),
(1, 8, '내일도 감사한 마음으로 시작하겠다는 다짐이 인상적이에요. 이런 긍정적인 마인드셋이 지속적인 행복의 원천이 될 거예요.'),
(1, 9, '친구들과 함께 웃고 떠들며 스트레스를 해소한 것 같아 다행이에요. 사회적 활동이 정서적 안정에 큰 도움이 되거든요.'),

-- 일기 2: 새로운 도전 (user_id=1의 페르소나들이 분석)
(2, 1, '새로운 프로젝트 시작을 축하해요! 불확실함 속에서도 용기를 내는 모습이 정말 멋져요.'),
(2, 8, '책임감 있게 맡은 역할을 수행하려는 의지가 느껴져요. 체계적인 계획을 세운 것도 성공의 좋은 시작이에요.'),
(2, 1, '성장의 기회로 받아들이는 태도가 훌륭해요. 이런 마음가짐이라면 분명 좋은 결과가 있을 거예요.'),
(2, 9, '팀원들과의 협업에 대한 기대감이 느껴져요. 함께 일하는 과정에서 많은 것을 배울 수 있을 거예요.'),
(2, 8, '초심을 잃지 않겠다는 다짐이 인상적이에요. 프로젝트 진행 중 힘든 순간이 와도 이 마음을 기억해주세요.'),

-- 일기 3: 휴식의 시간 (user_id=1의 페르소나들이 분석)
(3, 1, '완전한 휴식을 취한 하루, 정말 필요했던 시간이었을 것 같아요. 때로는 아무것도 하지 않는 것이 최고의 치유예요.'),
(3, 8, '일상의 압박감을 내려놓고 온전히 자신에게 집중한 시간이 의미 있어 보여요. 이런 여유가 더 나은 성과로 이어질 거예요.'),
(3, 1, '좋아하는 것들로 하루를 채운 모습이 아름다워요. 커피, 음악, 요리... 모두 당신을 위한 소중한 시간이었네요.'),
(3, 9, '멍하니 구름을 바라보며 느낀 평온함이 전해져요. 이런 순간들이 마음의 안정을 가져다주죠.'),

-- 일기 4: 감사한 하루 (user_id=2의 페르소나들이 분석)
(4, 2, '타인의 친절을 알아보고 감사할 줄 아는 마음이 아름다워요. 이런 관찰력이 당신을 더 따뜻한 사람으로 만들어줄 거예요.'),
(4, 10, '작은 친절들이 큰 울림으로 다가온 하루였네요. 당신의 외향성이 이런 긍정적인 에너지를 더 잘 받아들이게 해주는 것 같아요.'),
(4, 11, '받은 친절을 다시 베풀겠다는 다짐이 정말 멋져요. 이런 선순환이 더 나은 세상을 만드는 거죠.'),
(4, 2, '동료의 도움에 감사하는 마음, 그리고 그것을 기억하는 당신의 태도가 인상적이에요.'),

-- 일기 5: 운동 시작 (user_id=2의 페르소나들이 분석)
(5, 2, '드디어 실행에 옮긴 운동 계획! 실천력이 대단해요. 꾸준함이 성공의 열쇠예요.'),
(5, 10, '아침 일찍 일어나서 활동적으로 하루를 시작한 모습이 정말 에너지 넘쳐요! 이런 활력이 하루 전체를 긍정적으로 만들어줄 거예요.'),
(5, 11, '운동 일지를 작성하기 시작한 것도 좋은 방법이에요. 기록을 통해 발전을 확인하면 동기부여가 될 거예요.'),
(5, 2, '페이스를 조절하며 20분을 뛴 것, 정말 잘했어요. 무리하지 않고 천천히 늘려가는 게 중요하죠.'),

-- 일기 6: 책 읽기 (user_id=3의 페르소나들이 분석)
(6, 3, '에세이를 통해 일상의 소중함을 발견한 시간이었네요. 독서가 주는 깊은 성찰의 순간이 느껴져요.'),
(6, 12, '작가의 문체에 마음이 편안해졌다니, 당신의 감수성이 예민한 것 같아요. 이런 섬세함이 삶을 더 풍요롭게 만들어요.'),
(6, 3, '"행복은 작은 순간들 속에 있다"는 구절에 공감하신 것처럼, 일상의 소소한 기쁨을 알아차리는 능력이 있으신 것 같아요.'),
(6, 12, '바쁜 삶 속에서도 독서의 시간을 가지려는 의지가 보여요. 이런 여유가 정신적 건강에 큰 도움이 될 거예요.');

-- Insert friends relationships (bidirectional)
INSERT INTO friends (user_id, friend_id) VALUES
(1, 2),
(2, 1),
(1, 3),
(3, 1),
(2, 4),
(4, 2);

-- Insert friend invitations
INSERT INTO friend_invitations (inviter_user_id, invitee_user_id, status) VALUES
(5, 1, 'PENDING'),
(3, 4, 'PENDING'),
(2, 5, 'ACCEPTED');

-- Update responded_at for accepted invitation
UPDATE friend_invitations SET responded_at = NOW() WHERE status = 'ACCEPTED';

-- Insert friend chat messages (사용자와 친구의 페르소나들 간의 대화)
-- room_id 1: user 1과 user 2의 대화방
INSERT INTO friend_chat_messages (room_id, sender_actor_id, message) VALUES
(1, 1, '안녕! 오늘 기분 어때?'),
(1, 10, '안녕하세요! 오늘 에너지가 넘쳐요! 아침부터 운동도 하고 왔어요.'),
(1, 7, '와, 대단해요! 저는 새로운 카페를 탐방하고 싶은데 같이 가실래요?'),
(1, 11, '좋은 생각이에요! 제가 알아본 조용하고 분위기 좋은 카페가 있는데 어떠세요?'),
(1, 8, '저도 참여하고 싶어요! 카페에서 오늘 할 일 계획도 같이 세우면 좋겠네요.'),
(1, 2, '다들 열정적이네! 나도 새로운 곳 가는 거 좋아해. 몇 시에 만날까?'),
(1, 10, '오후 2시는 어때요? 점심 먹고 여유롭게 만나요!'),
(1, 1, '좋아! 그럼 2시에 보자. 오랜만에 다같이 모이니까 기대돼.'),

-- room_id 2: user 1과 user 3의 대화방
(2, 1, '요즘 어떻게 지내? 책 읽기는 잘 되고 있어?'),
(2, 12, '책은 정말 좋은 친구예요... 요즘 더 깊이 있는 책들을 읽고 있어요. 가끔은 너무 몰입해서 현실을 잊어버리기도 해요.'),
(2, 7, '어떤 책 읽고 계세요? 저도 새로운 것을 배우는 걸 좋아하는데 추천해주실 수 있나요?'),
(2, 3, '최근에 심리학 관련 책을 읽었는데 정말 흥미로웠어. "생각에 관한 생각"이라는 책인데, 우리가 어떻게 판단하고 결정하는지에 대해 다뤄.'),
(2, 12, '그 책 저도 읽었어요! 특히 직관과 논리적 사고의 차이 부분이 인상적이었죠. 때로는 직관이 더 정확할 때가 있다는 게 신기했어요.'),
(2, 8, '흥미롭네요! 저는 항상 계획적으로 생각하는 편인데, 직관도 중요하다니 새로운 관점이에요.'),
(2, 1, '맞아, 균형이 중요한 것 같아. 너무 한쪽으로 치우치면 놓치는 게 많더라.'),

-- room_id 3: user 2와 user 4의 대화방
(3, 2, '오늘 날씨 정말 좋다! 밖에 나가서 뭔가 하고 싶은데.'),
(3, 4, '그러게요! 저도 집에만 있기 아까운 날씨예요. 공원에서 피크닉이라도 할까요?'),
(3, 10, '피크닉 좋아요! 제가 샌드위치랑 과일 준비해올게요. 다같이 먹으면 더 맛있을 거예요!'),
(3, 11, '저는 돗자리랑 음료 가져올게요. 사람들과 함께하는 시간이 정말 소중해요.'),
(3, 2, '역시 너희들이야! 그럼 오후 3시에 한강공원에서 만나자.'),
(3, 4, '네! 기타도 가져갈게요. 날씨 좋은 날 야외에서 음악 들으면 기분이 좋아질 거예요.'),
(3, 10, '와! 라이브 음악이라니! 정말 완벽한 피크닉이 될 것 같아요!'),
(3, 11, '모두가 각자의 재능을 나누는 시간이 되겠네요. 정말 기대돼요.');

-- Insert my chat messages (사용자와 자신의 페르소나들 간의 대화)
INSERT INTO my_chat_messages (user_id, sender_actor_id, message) VALUES
-- user 1과 자신의 페르소나들의 대화
(1, 1, '오늘 할 일을 정리해보자. 뭐부터 시작하면 좋을까?'),
(1, 7, '새로운 프로젝트 아이디어를 브레인스토밍하는 건 어때요? 창의적인 아침 시간을 활용하면 좋을 것 같아요!'),
(1, 8, '먼저 오늘의 우선순위를 정하는 게 중요해요. 긴급하고 중요한 일부터 차근차근 처리해나가야 해요.'),
(1, 9, '일도 중요하지만 친구와의 약속도 있잖아요. 사람들과의 관계도 소중히 여기는 시간을 가져요.'),
(1, 1, '다들 좋은 의견이야. 먼저 중요한 일 정리하고, 창의적인 작업도 하고, 저녁에는 친구들 만나는 걸로!'),
(1, 7, '완벽한 계획이에요! 다양한 경험을 하는 하루가 될 것 같아 설레요.'),
(1, 8, '시간 배분을 잘 해서 각각의 일에 충분한 시간을 할애하세요. 제가 일정 관리를 도와드릴게요.'),
(1, 9, '친구들과 만날 때는 마음을 열고 즐거운 시간 보내세요. 오늘의 스트레스를 다 날려버려요!'),

-- user 2와 자신의 페르소나들의 대화
(2, 2, '운동을 시작했는데 계속 할 수 있을지 걱정이야.'),
(2, 10, '걱정하지 마세요! 우리가 함께 할게요. 매일 조금씩이라도 움직이면 습관이 될 거예요!'),
(2, 11, '무리하지 말고 자신의 페이스대로 하세요. 다른 사람과 비교하지 말고 어제의 나보다 나은 오늘이면 충분해요.'),
(2, 2, '고마워. 너희가 있어서 든든해. 오늘도 20분은 꼭 뛰어야겠다.'),
(2, 10, '그래요! 제가 응원할게요! 운동 후에는 맛있는 거 먹으면서 자신에게 보상도 주세요!'),
(2, 11, '운동 일지에 오늘의 기분도 같이 적어보세요. 신체적 변화뿐만 아니라 정신적 변화도 중요하니까요.'),
(2, 2, '좋은 생각이야! 몸도 마음도 건강해지는 걸 기록으로 남겨야겠어.'),

-- user 3과 자신의 페르소나의 대화
(3, 3, '오늘 읽은 책에서 인상 깊은 구절이 있었어. "행복은 목적지가 아니라 여행 그 자체다"라는 말이야.'),
(3, 12, '정말 아름다운 구절이네요... 우리는 너무 자주 미래의 행복을 기다리며 현재를 놓치곤 하죠.'),
(3, 3, '맞아. 나도 항상 뭔가를 이루면 행복할 거라고 생각했는데, 지금 이 순간도 충분히 행복할 수 있다는 걸 깨달았어.'),
(3, 12, '가끔은 불안하고 걱정되는 마음이 들 때도 있지만... 그것도 삶의 일부라고 받아들이려고 노력 중이에요.'),
(3, 3, '그래, 모든 감정이 다 의미가 있는 것 같아. 불안도 우리를 보호하려는 마음에서 오는 거니까.'),
(3, 12, '오늘 저녁에는 차 한 잔 마시면서 오늘 하루를 돌아보는 시간을 가져보는 건 어때요?'),
(3, 3, '좋아. 조용히 하루를 마무리하는 시간을 갖는 것도 중요하지.');

-- Insert game sessions
INSERT INTO game_sessions (user_id, game_type, finished_at) VALUES
(1, 'BART', NOW() - INTERVAL '7 days'),
(1, 'GNG', NOW() - INTERVAL '6 days'),
(1, 'UG', NOW() - INTERVAL '5 days'),
(1, 'BART', NOW() - INTERVAL '3 days'),
(1, 'GNG', NOW() - INTERVAL '2 days'),
(2, 'BART', NOW() - INTERVAL '4 days'),
(2, 'GNG', NOW() - INTERVAL '3 days'),
(2, 'UG', NOW() - INTERVAL '1 day'),
(3, 'UG', NOW() - INTERVAL '2 days'),
(3, 'BART', NOW() - INTERVAL '1 day'),
(4, 'GNG', NOW()),
(5, 'BART', NULL);

-- Insert BART game data (Balloon Analogue Risk Task)
INSERT INTO game_bart_responses (session_id, round_index, color, is_popped, popping_point, pumping_cnt, finished_at) VALUES
-- Session 1 (user 1) - 완료된 전체 게임
(1, 1, 'RED', false, 15, 12, NOW() - INTERVAL '7 days'),
(1, 2, 'BLUE', true, 20, 22, NOW() - INTERVAL '7 days'),
(1, 3, 'GREEN', false, 25, 20, NOW() - INTERVAL '7 days'),
(1, 4, 'RED', false, 18, 15, NOW() - INTERVAL '7 days'),
(1, 5, 'BLUE', false, 22, 18, NOW() - INTERVAL '7 days'),
(1, 6, 'GREEN', true, 28, 29, NOW() - INTERVAL '7 days'),
(1, 7, 'RED', false, 16, 14, NOW() - INTERVAL '7 days'),
(1, 8, 'BLUE', true, 24, 25, NOW() - INTERVAL '7 days'),

-- Session 4 (user 1) - 두 번째 BART 게임
(4, 1, 'GREEN', false, 30, 25, NOW() - INTERVAL '3 days'),
(4, 2, 'RED', false, 17, 14, NOW() - INTERVAL '3 days'),
(4, 3, 'BLUE', false, 26, 22, NOW() - INTERVAL '3 days'),
(4, 4, 'GREEN', true, 32, 33, NOW() - INTERVAL '3 days'),
(4, 5, 'RED', false, 19, 16, NOW() - INTERVAL '3 days'),

-- Session 6 (user 2)
(6, 1, 'RED', false, 20, 18, NOW() - INTERVAL '4 days'),
(6, 2, 'GREEN', true, 25, 26, NOW() - INTERVAL '4 days'),
(6, 3, 'BLUE', false, 28, 24, NOW() - INTERVAL '4 days'),
(6, 4, 'RED', false, 16, 13, NOW() - INTERVAL '4 days'),

-- Session 10 (user 3)
(10, 1, 'BLUE', false, 30, 26, NOW() - INTERVAL '1 day'),
(10, 2, 'GREEN', false, 28, 24, NOW() - INTERVAL '1 day'),
(10, 3, 'RED', true, 18, 19, NOW() - INTERVAL '1 day'),
(10, 4, 'BLUE', false, 25, 21, NOW() - INTERVAL '1 day'),
(10, 5, 'GREEN', false, 27, 23, NOW() - INTERVAL '1 day');

-- Insert BART clicks data (각 펌핑의 타이밍 기록)
INSERT INTO game_bart_clicks (response_id, click_index, clicked_at) VALUES
(1, 1, NOW() - INTERVAL '7 days' - INTERVAL '30 seconds'),
(1, 2, NOW() - INTERVAL '7 days' - INTERVAL '28 seconds'),
(1, 3, NOW() - INTERVAL '7 days' - INTERVAL '26 seconds'),
(1, 4, NOW() - INTERVAL '7 days' - INTERVAL '24 seconds'),
(1, 5, NOW() - INTERVAL '7 days' - INTERVAL '22 seconds'),
(1, 6, NOW() - INTERVAL '7 days' - INTERVAL '20 seconds'),
(1, 7, NOW() - INTERVAL '7 days' - INTERVAL '18 seconds'),
(1, 8, NOW() - INTERVAL '7 days' - INTERVAL '16 seconds'),
(1, 9, NOW() - INTERVAL '7 days' - INTERVAL '14 seconds'),
(1, 10, NOW() - INTERVAL '7 days' - INTERVAL '12 seconds'),
(1, 11, NOW() - INTERVAL '7 days' - INTERVAL '10 seconds'),
(1, 12, NOW() - INTERVAL '7 days' - INTERVAL '8 seconds');

INSERT INTO game_bart_results (session_id, total_balloons, success_balloons, fail_balloons, avg_pumps, reward_amount, missed_reward, computed_at) VALUES
(1, 8, 5, 3, 18.625, 890, 540, NOW() - INTERVAL '7 days'),
(4, 5, 4, 1, 19.40, 770, 330, NOW() - INTERVAL '3 days'),
(6, 4, 3, 1, 18.75, 550, 260, NOW() - INTERVAL '4 days'),
(10, 5, 4, 1, 23.60, 940, 190, NOW() - INTERVAL '1 day');

-- Insert GNG game data (Go/No-Go Task)
INSERT INTO game_gng_responses (session_id, trial_index, stimulus_type, trial_started_at, stimulus_appeared_at, responded_at, is_succeeded) VALUES
-- Session 2 (user 1)
(2, 1, 'GO', NOW() - INTERVAL '6 days' - INTERVAL '5 minutes', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 59 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 58.5 seconds', true),
(2, 2, 'NOGO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 57 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 56 seconds', NULL, true),
(2, 3, 'GO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 54 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 53 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 52.3 seconds', true),
(2, 4, 'GO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 51 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 50 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 49.4 seconds', true),
(2, 5, 'NOGO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 48 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 47 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 46.8 seconds', false),
(2, 6, 'GO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 45 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 44 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 43.2 seconds', true),
(2, 7, 'NOGO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 42 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 41 seconds', NULL, true),
(2, 8, 'GO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 39 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 38 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 37.6 seconds', true),
(2, 9, 'GO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 36 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 35 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 34.4 seconds', true),
(2, 10, 'NOGO', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 33 seconds', NOW() - INTERVAL '6 days' - INTERVAL '4 minutes 32 seconds', NULL, true),

-- Session 5 (user 1) - 두 번째 GNG
(5, 1, 'GO', NOW() - INTERVAL '2 days' - INTERVAL '3 minutes', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 59 seconds', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 58.3 seconds', true),
(5, 2, 'GO', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 57 seconds', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 56 seconds', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 55.4 seconds', true),
(5, 3, 'NOGO', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 54 seconds', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 53 seconds', NULL, true),
(5, 4, 'GO', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 51 seconds', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 50 seconds', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 49.2 seconds', true),
(5, 5, 'NOGO', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 48 seconds', NOW() - INTERVAL '2 days' - INTERVAL '2 minutes 47 seconds', NULL, true),

-- Session 7 (user 2)
(7, 1, 'GO', NOW() - INTERVAL '3 days' - INTERVAL '2 minutes', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 59 seconds', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 58.2 seconds', true),
(7, 2, 'NOGO', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 57 seconds', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 56 seconds', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 55.5 seconds', false),
(7, 3, 'GO', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 54 seconds', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 53 seconds', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 52.1 seconds', true),
(7, 4, 'GO', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 51 seconds', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 50 seconds', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 49.3 seconds', true),
(7, 5, 'NOGO', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 48 seconds', NOW() - INTERVAL '3 days' - INTERVAL '1 minute 47 seconds', NULL, true),

-- Session 11 (user 4)
(11, 1, 'GO', NOW() - INTERVAL '1 minute', NOW() - INTERVAL '59 seconds', NOW() - INTERVAL '58.4 seconds', true),
(11, 2, 'GO', NOW() - INTERVAL '57 seconds', NOW() - INTERVAL '56 seconds', NOW() - INTERVAL '55.3 seconds', true),
(11, 3, 'NOGO', NOW() - INTERVAL '54 seconds', NOW() - INTERVAL '53 seconds', NULL, true);

INSERT INTO game_gng_results (session_id, played_at, finished_at, total_correct_cnt, total_incorrect_cnt, nogo_incorrect_cnt, avg_reaction_time) VALUES
(2, NOW() - INTERVAL '6 days' - INTERVAL '5 minutes', NOW() - INTERVAL '6 days', 9, 1, 1, 0.58),
(5, NOW() - INTERVAL '2 days' - INTERVAL '3 minutes', NOW() - INTERVAL '2 days', 5, 0, 0, 0.52),
(7, NOW() - INTERVAL '3 days' - INTERVAL '2 minutes', NOW() - INTERVAL '3 days', 4, 1, 1, 0.63),
(11, NOW() - INTERVAL '1 minute', NOW(), 3, 0, 0, 0.45);

-- Insert UG game order data (Ultimatum Game의 시나리오)
INSERT INTO game_ug_order (money, persona_type, rate, role_type) VALUES
('고액', '가족', 0.5, 1),
('소액', '친구', 0.3, 2),
('고액', '낯선사람', 0.7, 1),
('소액', '가족', 0.4, 2),
('고액', '친구', 0.6, 1),
('소액', '낯선사람', 0.2, 2),
('고액', '가족', 0.45, 1),
('소액', '친구', 0.35, 2),
('고액', '낯선사람', 0.8, 1),
('소액', '가족', 0.55, 2);

-- Insert UG game responses
INSERT INTO game_ug_responses (session_id, order_id, money, is_accepted, finished_at) VALUES
-- Session 3 (user 1)
(3, 1, 50000, true, NOW() - INTERVAL '5 days'),
(3, 2, 10000, false, NOW() - INTERVAL '5 days'),
(3, 3, 80000, true, NOW() - INTERVAL '5 days'),
(3, 4, 15000, true, NOW() - INTERVAL '5 days'),
(3, 5, 60000, true, NOW() - INTERVAL '5 days'),

-- Session 8 (user 2)
(8, 6, 5000, false, NOW() - INTERVAL '1 day'),
(8, 7, 45000, true, NOW() - INTERVAL '1 day'),
(8, 8, 12000, true, NOW() - INTERVAL '1 day'),
(8, 9, 100000, false, NOW() - INTERVAL '1 day'),

-- Session 9 (user 3)
(9, 1, 50000, true, NOW() - INTERVAL '2 days'),
(9, 3, 70000, true, NOW() - INTERVAL '2 days'),
(9, 5, 65000, true, NOW() - INTERVAL '2 days'),
(9, 10, 20000, true, NOW() - INTERVAL '2 days');

INSERT INTO game_ug_results (session_id, earned_amount, finished_at) VALUES
(3, 105000, NOW() - INTERVAL '5 days'),
(8, 57000, NOW() - INTERVAL '1 day'),
(9, 102500, NOW() - INTERVAL '2 days');

-- Insert game session results (Big Five 성격 점수)
INSERT INTO game_session_results (session_id, user_id, result_o, result_c, result_e, result_a, result_n) VALUES
(1, 1, 75, 80, 70, 65, 40),
(2, 1, 78, 82, 72, 68, 38),
(3, 1, 76, 81, 71, 70, 37),
(4, 1, 77, 83, 73, 69, 36),
(5, 1, 79, 84, 74, 71, 35),
(6, 2, 65, 70, 85, 75, 45),
(7, 2, 67, 72, 87, 77, 43),
(8, 2, 68, 73, 88, 78, 42),
(9, 3, 70, 75, 60, 80, 50),
(10, 3, 72, 77, 62, 82, 48),
(11, 4, 85, 65, 75, 70, 55);

-- Insert reports
INSERT INTO reports (user_id, report_type, content) VALUES
(1, 'SELF', '{"summary": "자기 평가 보고서", "scores": {"O": 75, "C": 80, "E": 70, "A": 65, "N": 40}}'::jsonb),
(1, 'FINAL', '{"summary": "최종 평가 보고서", "scores": {"O": 78, "C": 82, "E": 72, "A": 68, "N": 38}}'::jsonb),
(2, 'SELF', '{"summary": "자기 평가 보고서", "scores": {"O": 65, "C": 70, "E": 85, "A": 75, "N": 45}}'::jsonb);

-- ===========================================
-- 사용자별 테스트 케이스 시나리오 요약
-- ===========================================

/*
🔧 페르소나 생성 규칙: 설문 100문항 완료 + 모든 게임(BART, GNG, UG) 완료 시에만 생성

TEST CASE 1: User 1 (바다사랑) - 완전한 활동 사용자 ✅
- 프로필: 창의적이고 성실한 성격 (O↑, C↑, E중, A중, N↓)
- 성격검사: 완료 (100문항 모두 응답)ㄴㄴㄴㄴㄴ
- 일기: 3개 작성 (감정일기, 도전, 휴식)
- 게임: 모든 게임 완료 (BART 2회, GNG 2회, UG 1회)
- 페르소나: 3개 생성됨 (호기심이-O, 성실이-C, 활발이-E) ✅
- 친구관계: User 2, 3과 친구
- 채팅: 일기분석, 친구채팅, 내면대화 모두 활발
- 보고서: SELF, FINAL 보고서 생성됨
- 사용사례: 모든 기능을 완주한 파워유저

TEST CASE 2: User 2 (파도타기) - 사교적 활동 사용자 ✅
- 프로필: 외향적이고 친화적인 성격 (E↑, A↑, C중, O중, N중)
- 성격검사: 완료 (100문항 모두 응답)
- 일기: 2개 작성 (감사한 하루, 운동 시작)
- 게임: 모든 게임 완료 (BART 1회, GNG 1회, UG 1회)
- 페르소나: 2개 생성됨 (친화력-E, 배려심-A) ✅
- 친구관계: User 1, 4와 친구
- 채팅: 주로 친구들과의 소통에 집중
- 보고서: SELF 보고서만 생성됨
- 사용사례: 사회적 기능을 적극 활용하는 완료형 사용자

TEST CASE 3: User 3 (깊은바다) - 부분 완료 사용자 ❌
- 프로필: 내성적이고 신중한 성격
- 성격검사: 미완료 (O, C, E 요인만 60문항 응답, A, N 미응답)
- 일기: 1개 작성 (책 읽기)
- 게임: 부분 완료 (UG, BART 각 1회, GNG 미완료)
- 페르소나: 생성 불가 ❌ (조건 미충족)
- 친구관계: User 1과만 친구
- 채팅: 제한적 활동 (페르소나 없음)
- 보고서: 생성되지 않음
- 사용사례: 중도 이탈 사용자, 페르소나 기능 미경험

TEST CASE 4: User 4 (산호초) - 신규 사용자 ❌
- 프로필: 가입만 완료된 상태
- 성격검사: 미시작 (0문항)
- 일기: 작성하지 않음
- 게임: 진행 중 (GNG 현재 플레이 중, BART/UG 미시작)
- 페르소나: 생성 불가 ❌ (조건 미충족)
- 친구관계: User 2와 친구
- 채팅: 활동 없음
- 보고서: 생성되지 않음
- 사용사례: 온보딩 과정의 신규 사용자

TEST CASE 5: User 5 (돌고래) - 비활성 사용자 ❌
- 프로필: 가입만 완료된 상태
- 성격검사: 미시작 (0문항)
- 일기: 작성하지 않음
- 게임: 중단 상태 (BART 시작했지만 미완료, finished_at = NULL)
- 페르소나: 생성 불가 ❌ (조건 미충족)
- 친구관계: 초대만 받음 (친구 관계 미성립)
- 채팅: 활동 없음
- 보고서: 생성되지 않음
- 사용사례: 가입 후 비활성화된 사용자

🎯 테스트 검증 포인트:
1. 페르소나 생성 조건 검증 (User 1, 2만 생성됨)
2. 120문항 설문 완료 여부 (User 1, 2만 완료)
3. 모든 게임 완료 여부 (User 1, 2만 완료)
4. 부분 완료 사용자 처리 (User 3: 페르소나 없음)
5. 신규/비활성 사용자 관리 (User 4, 5: 페르소나 없음)
*/

-- Display summary
SELECT 'Seed data inserted successfully!' as message;
SELECT 'Users created: ' || COUNT(*) as summary FROM users;
SELECT 'Diaries created: ' || COUNT(*) as summary FROM diaries;
SELECT 'Game sessions created: ' || COUNT(*) as summary FROM game_sessions;
SELECT 'Friend relationships created: ' || COUNT(*) as summary FROM friends;