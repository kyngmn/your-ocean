''-- Insert Big Five codes (Big5 성격 검사 요인)
-- O: 개방성 (Openness)
INSERT INTO big_five_codes (big_code, small_code, content) VALUES
('O','O1','상상력(Imagination)'),
('O','O2','예술적 관심(Artistic Interests)'),
('O','O3','감정성(Emotionality)'),
('O','O4','모험성(Adventurousness)'),
('O','O5','지적 호기심(Intellect)'),
('O','O6','자유주의(Liberalism)');

-- C: 성실성 (Conscientiousness)
INSERT INTO big_five_codes (big_code, small_code, content) VALUES
('C','C1','자기효능감(Self-efficacy)'),
('C','C2','질서성(Orderliness)'),
('C','C3','책임감(Dutifulness)'),
('C','C4','성취추구(Achievement Striving)'),
('C','C5','자기통제(Self-discipline)'),
('C','C6','신중함(Cautiousness)');

-- E: 외향성 (Extraversion)
INSERT INTO big_five_codes (big_code, small_code, content) VALUES
('E','E1','친밀감(Friendliness)'),
('E','E2','사교성(Gregariousness)'),
('E','E3','자기주장(Assertiveness)'),
('E','E4','활동 수준(Activity Level)'),
('E','E5','자극 추구(Excitement Seeking)'),
('E','E6','쾌활성(Cheerfulness)');

-- A: 친화성 (Agreeableness)
INSERT INTO big_five_codes (big_code, small_code, content) VALUES
('A','A1','신뢰(Trust)'),
('A','A2','도덕성(Morality)'),
('A','A3','이타심(Altruism)'),
('A','A4','협동(Cooperation)'),
('A','A5','겸손(Modesty)'),
('A','A6','연민(Sympathy)');

-- N: 신경성 (Neuroticism)
INSERT INTO big_five_codes (big_code, small_code, content) VALUES
('N','N1','불안(Anxiety)'),
('N','N2','분노(Anger)'),
('N','N3','우울(Depression)'),
('N','N4','자의식(Self-consciousness)'),
('N','N5','충동성(Immoderation)'),
('N','N6','취약성(Vulnerability)');

-- N1: Anxiety
INSERT INTO surveys VALUES
(1,  (SELECT id FROM big_five_codes WHERE small_code='N1'), false, '나는 사소한 일에도 걱정을 많이 하는 편이다.'),
(31, (SELECT id FROM big_five_codes WHERE small_code='N1'), false, '나는 최악의 상황을 두려워하는 편이다.'),
(61, (SELECT id FROM big_five_codes WHERE small_code='N1'), false, '나는 많은 것들을 두려워한다.'),
(91, (SELECT id FROM big_five_codes WHERE small_code='N1'), false, '나는 쉽게 스트레스를 받는 편이다.');

-- N2: Anger
INSERT INTO surveys VALUES
(6,  (SELECT id FROM big_five_codes WHERE small_code='N2'), false, '나는 쉽게 화를 내는 편이다.'),
(36, (SELECT id FROM big_five_codes WHERE small_code='N2'), false, '나는 쉽게 짜증이 나는 편이다.'),
(66, (SELECT id FROM big_five_codes WHERE small_code='N2'), false, '나는 자주 화를 낸다.'),
(96, (SELECT id FROM big_five_codes WHERE small_code='N2'), true,  '나는 좀처럼 짜증을 내지 않는다.');

-- N3: Depression
INSERT INTO surveys VALUES
(11,  (SELECT id FROM big_five_codes WHERE small_code='N3'), false, '나는 자주 우울감을 느끼는 편이다.'),
(41,  (SELECT id FROM big_five_codes WHERE small_code='N3'), false, '나는 자신을 싫어하는 편이다.'),
(71,  (SELECT id FROM big_five_codes WHERE small_code='N3'), false, '나는 자주 우울감에 빠진다.'),
(101, (SELECT id FROM big_five_codes WHERE small_code='N3'), false, '나는 나 자신에 대해 낮게 평가한다.');

-- N4: Self-consciousness
INSERT INTO surveys VALUES
(16,  (SELECT id FROM big_five_codes WHERE small_code='N4'), false, '나는 다른 사람에게 먼저 다가가기 어렵다.'),
(46,  (SELECT id FROM big_five_codes WHERE small_code='N4'), false, '나는 쉽게 위축되는 편이다.'),
(76,  (SELECT id FROM big_five_codes WHERE small_code='N4'), true,  '나는 쉽게 당황하지 않는 편이다.'),
(106, (SELECT id FROM big_five_codes WHERE small_code='N4'), true,  '나는 내 의견을 당당히 말할 수 있다.');

-- N5: Immoderation
INSERT INTO surveys VALUES
(21,  (SELECT id FROM big_five_codes WHERE small_code='N5'), false, '나는 음식을 자주 과식하는 편이다.'),
(51,  (SELECT id FROM big_five_codes WHERE small_code='N5'), false, '나는 폭식이나 폭음 같은 과한 행동을 하는 편이다.'),
(81,  (SELECT id FROM big_five_codes WHERE small_code='N5'), true,  '나는 과도하게 몰두하는 일이 드물다.'),
(111, (SELECT id FROM big_five_codes WHERE small_code='N5'), true,  '나는 욕구를 잘 절제할 수 있다.');

-- N6: Vulnerability
INSERT INTO surveys VALUES
(26,  (SELECT id FROM big_five_codes WHERE small_code='N6'), false, '나는 일을 감당하기 힘들다고 느낄 때가 많다.'),
(56,  (SELECT id FROM big_five_codes WHERE small_code='N6'), true,  '나는 압박 속에서도 침착함을 유지한다.'),
(86,  (SELECT id FROM big_five_codes WHERE small_code='N6'), true,  '나는 어려움에 잘 대처할 줄 안다.'),
(116, (SELECT id FROM big_five_codes WHERE small_code='N6'), true,  '나는 긴장된 상황에서도 침착하다.');

-- E1: Friendliness
INSERT INTO surveys VALUES
(2,  (SELECT id FROM big_five_codes WHERE small_code='E1'), false, '나는 쉽게 친구를 사귀는 편이다.'),
(32, (SELECT id FROM big_five_codes WHERE small_code='E1'), false, '나는 다른 사람과 금세 친해지는 편이다.'),
(62, (SELECT id FROM big_five_codes WHERE small_code='E1'), false, '나는 사람들 사이에서 편안함을 느낀다.'),
(92, (SELECT id FROM big_five_codes WHERE small_code='E1'), false, '나는 사람들과 함께 있을 때 편안하다.');

-- E2: Gregariousness
INSERT INTO surveys VALUES
(7,  (SELECT id FROM big_five_codes WHERE small_code='E2'), false, '나는 사람들이 많이 모이는 자리를 좋아한다.'),
(37, (SELECT id FROM big_five_codes WHERE small_code='E2'), false, '나는 모임에서 다양한 사람들과 이야기하는 편이다.'),
(67, (SELECT id FROM big_five_codes WHERE small_code='E2'), true,  '나는 사람들이 붐비는 모임을 좋아하지 않는다.'),
(97, (SELECT id FROM big_five_codes WHERE small_code='E2'), true,  '나는 사람이 많은 곳을 피한다.');

-- E3: Assertiveness
INSERT INTO surveys VALUES
(12,  (SELECT id FROM big_five_codes WHERE small_code='E3'), false, '나는 상황을 주도하려는 편이다.'),
(42,  (SELECT id FROM big_five_codes WHERE small_code='E3'), false, '나는 다른 사람을 이끌려고 한다.'),
(72,  (SELECT id FROM big_five_codes WHERE small_code='E3'), false, '나는 상황을 통제하려 한다.'),
(102, (SELECT id FROM big_five_codes WHERE small_code='E3'), true,  '나는 타인이 먼저 나서길 기다리는 편이다.');

-- E4: Activity Level
INSERT INTO surveys VALUES
(17,  (SELECT id FROM big_five_codes WHERE small_code='E4'), false, '나는 늘 바쁘게 지내는 편이다.'),
(47,  (SELECT id FROM big_five_codes WHERE small_code='E4'), false, '나는 늘 바쁘게 움직이는 편이다.'),
(77,  (SELECT id FROM big_five_codes WHERE small_code='E4'), false, '나는 여가 시간에 많은 일을 한다.'),
(107, (SELECT id FROM big_five_codes WHERE small_code='E4'), false, '나는 동시에 많은 일을 해낼 수 있다.');

-- E5: Excitement Seeking
INSERT INTO surveys VALUES
(22,  (SELECT id FROM big_five_codes WHERE small_code='E5'), false, '나는 흥분되는 상황을 좋아한다.'),
(52,  (SELECT id FROM big_five_codes WHERE small_code='E5'), false, '나는 모험을 추구한다.'),
(82,  (SELECT id FROM big_five_codes WHERE small_code='E5'), false, '나는 활동적인 것을 좋아한다.'),
(112, (SELECT id FROM big_five_codes WHERE small_code='E5'), false, '나는 무모한 행동을 즐기는 편이다.');

-- E6: Cheerfulness
INSERT INTO surveys VALUES
(27,  (SELECT id FROM big_five_codes WHERE small_code='E6'), false, '나는 즐거움을 잘 드러내는 편이다.'),
(57,  (SELECT id FROM big_five_codes WHERE small_code='E6'), false, '나는 즐거움을 많이 느끼는 편이다.'),
(87,  (SELECT id FROM big_five_codes WHERE small_code='E6'), false, '나는 삶을 사랑한다.'),
(117, (SELECT id FROM big_five_codes WHERE small_code='E6'), false, '나는 크게 웃는 편이다.');

-- O1: Imagination
INSERT INTO surveys VALUES
(3,  (SELECT id FROM big_five_codes WHERE small_code='O1'), false, '나는 상상력이 풍부한 편이다.'),
(33, (SELECT id FROM big_five_codes WHERE small_code='O1'), false, '나는 공상을 좋아하는 편이다.'),
(63, (SELECT id FROM big_five_codes WHERE small_code='O1'), false, '나는 공상에 빠지는 것을 좋아한다.'),
(93, (SELECT id FROM big_five_codes WHERE small_code='O1'), false, '나는 혼자 사색하는 것을 좋아한다.');

-- O2: Artistic Interests
INSERT INTO surveys VALUES
(8,  (SELECT id FROM big_five_codes WHERE small_code='O2'), false, '나는 다른 사람들이 잘 보지 못하는 아름다움을 발견하는 편이다.'),
(38, (SELECT id FROM big_five_codes WHERE small_code='O2'), true,  '나는 예술을 좋아하지 않는다.'),
(68, (SELECT id FROM big_five_codes WHERE small_code='O2'), true,  '나는 시를 좋아하지 않는다.'),
(98, (SELECT id FROM big_five_codes WHERE small_code='O2'), true,  '나는 미술관에 가는 것을 즐기지 않는다.');

-- O3: Emotionality
INSERT INTO surveys VALUES
(13,  (SELECT id FROM big_five_codes WHERE small_code='O3'), false, '나는 감정을 강하게 느끼는 편이다.'),
(43,  (SELECT id FROM big_five_codes WHERE small_code='O3'), true,  '나는 감정이 잘 드러나지 않는 편이다.'),
(73,  (SELECT id FROM big_five_codes WHERE small_code='O3'), true,  '나는 감정에 쉽게 휘둘리지 않는다.'),
(103, (SELECT id FROM big_five_codes WHERE small_code='O3'), true,  '나는 감정적 기복이 거의 없다.');

-- O4: Adventurousness
INSERT INTO surveys VALUES
(18,  (SELECT id FROM big_five_codes WHERE small_code='O4'), true,  '나는 새로운 것보다는 익숙한 것을 고수하는 편이다.'),
(48,  (SELECT id FROM big_five_codes WHERE small_code='O4'), true,  '나는 변화를 좋아하지 않는다.'),
(78,  (SELECT id FROM big_five_codes WHERE small_code='O4'), true,  '나는 변화라는 개념을 좋아하지 않는다.'),
(108, (SELECT id FROM big_five_codes WHERE small_code='O4'), true,  '나는 전통적인 방식을 고수하는 편이다.');

-- O5: Intellect
INSERT INTO surveys VALUES
(23,  (SELECT id FROM big_five_codes WHERE small_code='O5'), true,  '나는 추상적인 아이디어에는 관심이 없는 편이다.'),
(53,  (SELECT id FROM big_five_codes WHERE small_code='O5'), true,  '나는 철학적 논의를 피하는 편이다.'),
(83,  (SELECT id FROM big_five_codes WHERE small_code='O5'), true,  '나는 추상적인 개념을 이해하기 어렵다.'),
(113, (SELECT id FROM big_five_codes WHERE small_code='O5'), true,  '나는 이론적인 토론에는 관심이 없다.');

-- O6: Liberalism
INSERT INTO surveys VALUES
(28,  (SELECT id FROM big_five_codes WHERE small_code='O6'), false, '나는 진보적인 정치 성향의 후보에게 투표하는 편이다.'),
(58,  (SELECT id FROM big_five_codes WHERE small_code='O6'), true,  '나는 단 하나의 진정한 종교를 믿는다.'),
(88,  (SELECT id FROM big_five_codes WHERE small_code='O6'), true,  '나는 보수 정치 후보에게 투표하는 경향이 있다.'),
(118, (SELECT id FROM big_five_codes WHERE small_code='O6'), true,  '나는 국가의식 행사(국기에 대한 맹세 등)에 기꺼이 참여하는 편이다.');

-- A1: Trust
INSERT INTO surveys VALUES
(4,   (SELECT id FROM big_five_codes WHERE small_code='A1'), false, '나는 다른 사람을 잘 믿는 편이다.'),
(34,  (SELECT id FROM big_five_codes WHERE small_code='A1'), false, '나는 다른 사람들이 선의를 가지고 있다고 믿는 편이다.'),
(64,  (SELECT id FROM big_five_codes WHERE small_code='A1'), false, '나는 사람들이 하는 말을 잘 믿는다.'),
(94,  (SELECT id FROM big_five_codes WHERE small_code='A1'), true,  '나는 사람들을 잘 믿지 않는다.');

-- A2: Morality
INSERT INTO surveys VALUES
(9,   (SELECT id FROM big_five_codes WHERE small_code='A2'), true,  '나는 아부를 이용해 이익을 얻으려는 편이다.'),
(39,  (SELECT id FROM big_five_codes WHERE small_code='A2'), true,  '나는 규칙을 피하는 방법을 잘 안다.'),
(69,  (SELECT id FROM big_five_codes WHERE small_code='A2'), true,  '나는 다른 사람을 속여서라도 앞서 나가려는 편이다.'),
(99,  (SELECT id FROM big_five_codes WHERE small_code='A2'), true,  '나는 다른 사람을 이용하는 편이다.');

-- A3: Altruism
INSERT INTO surveys VALUES
(14,  (SELECT id FROM big_five_codes WHERE small_code='A3'), false, '나는 다른 사람을 따뜻하게 맞이하는 편이다.'),
(44,  (SELECT id FROM big_five_codes WHERE small_code='A3'), false, '나는 다른 사람을 돕는 것을 좋아한다.'),
(74,  (SELECT id FROM big_five_codes WHERE small_code='A3'), false, '나는 타인을 배려하는 편이다.'),
(104, (SELECT id FROM big_five_codes WHERE small_code='A3'), true,  '나는 타인에게 등을 돌린다.');

-- A4: Cooperation
INSERT INTO surveys VALUES
(19,  (SELECT id FROM big_five_codes WHERE small_code='A4'), true,  '나는 건강한 논쟁을 좋아하는 편이다.'),
(49,  (SELECT id FROM big_five_codes WHERE small_code='A4'), true,  '나는 자주 소리를 지른다.'),
(79,  (SELECT id FROM big_five_codes WHERE small_code='A4'), true,  '나는 다른 사람을 모욕하는 경우가 있다.'),
(109, (SELECT id FROM big_five_codes WHERE small_code='A4'), true,  '나는 다른 사람에게 되갚음을 하려는 편이다.');

-- A5: Modesty
INSERT INTO surveys VALUES
(24,  (SELECT id FROM big_five_codes WHERE small_code='A5'), true,  '나는 내가 다른 사람보다 낫다고 생각하는 편이다.'),
(54,  (SELECT id FROM big_five_codes WHERE small_code='A5'), true,  '나는 나 자신을 높이 평가한다.'),
(84,  (SELECT id FROM big_five_codes WHERE small_code='A5'), true,  '나는 나 자신을 높게 평가하는 편이다.'),
(114, (SELECT id FROM big_five_codes WHERE small_code='A5'), true,  '나는 스스로를 주목받게 만드는 편이다.');

-- A6: Sympathy
INSERT INTO surveys VALUES
(29,  (SELECT id FROM big_five_codes WHERE small_code='A6'), false, '나는 노숙자나 어려운 사람들에게 연민을 느낀다.'),
(59,  (SELECT id FROM big_five_codes WHERE small_code='A6'), false, '나는 나보다 어려운 처지에 있는 이들에게 동정심을 느낀다.'),
(89,  (SELECT id FROM big_five_codes WHERE small_code='A6'), false, '나는 타인의 슬픔에 공감하는 편이다.'),
(119, (SELECT id FROM big_five_codes WHERE small_code='A6'), true,  '나는 다른 사람의 문제에 관심이 없는 편이다.');

-- C1: Self-efficacy
INSERT INTO surveys VALUES
(5,   (SELECT id FROM big_five_codes WHERE small_code='C1'), false, '나는 맡은 일을 잘 끝내는 편이다.'),
(35,  (SELECT id FROM big_five_codes WHERE small_code='C1'), false, '나는 내가 하는 일에서 뛰어난 성과를 내는 편이다.'),
(65,  (SELECT id FROM big_five_codes WHERE small_code='C1'), false, '나는 일을 매끄럽게 처리하는 편이다.'),
(95,  (SELECT id FROM big_five_codes WHERE small_code='C1'), false, '나는 일을 잘 해내는 방법을 알고 있다.');

-- C2: Orderliness
INSERT INTO surveys VALUES
(10,  (SELECT id FROM big_five_codes WHERE small_code='C2'), false, '나는 질서 정연한 것을 좋아한다.'),
(40,  (SELECT id FROM big_five_codes WHERE small_code='C2'), false, '나는 정리정돈을 좋아한다.'),
(70,  (SELECT id FROM big_five_codes WHERE small_code='C2'), true,  '나는 방이나 물건을 어질러 놓는 편이다.'),
(100, (SELECT id FROM big_five_codes WHERE small_code='C2'), true,  '나는 물건을 아무 데나 두는 편이다.');

-- C3: Dutifulness
INSERT INTO surveys VALUES
(15,  (SELECT id FROM big_five_codes WHERE small_code='C3'), false, '나는 약속을 잘 지키는 편이다.'),
(45,  (SELECT id FROM big_five_codes WHERE small_code='C3'), false, '나는 진실을 말하는 편이다.'),
(75,  (SELECT id FROM big_five_codes WHERE small_code='C3'), true,  '나는 약속을 잘 어긴다.'),
(105, (SELECT id FROM big_five_codes WHERE small_code='C3'), true,  '나는 내 할 일을 다른 사람에게 떠넘기는 편이다.');

-- C4: Achievement Striving
INSERT INTO surveys VALUES
(20,  (SELECT id FROM big_five_codes WHERE small_code='C4'), false, '나는 열심히 일하는 편이다.'),
(50,  (SELECT id FROM big_five_codes WHERE small_code='C4'), false, '나는 기대 이상의 일을 해내는 편이다.'),
(80,  (SELECT id FROM big_five_codes WHERE small_code='C4'), false, '나는 나와 다른 사람에게 높은 기준을 세우는 편이다.'),
(110, (SELECT id FROM big_five_codes WHERE small_code='C4'), true,  '나는 성공하려는 의지가 강하지 않다.');

-- C5: Self-discipline
INSERT INTO surveys VALUES
(25,  (SELECT id FROM big_five_codes WHERE small_code='C5'), false, '나는 일을 바로 시작하는 편이다.'),
(55,  (SELECT id FROM big_five_codes WHERE small_code='C5'), true,  '나는 일을 시작하기 어려워한다.'),
(85,  (SELECT id FROM big_five_codes WHERE small_code='C5'), true,  '나는 일을 시작할 때 다른 사람의 자극이 필요하다.'),
(115, (SELECT id FROM big_five_codes WHERE small_code='C5'), true,  '나는 일을 시작하기 어려워한다.');

-- C6: Cautiousness
INSERT INTO surveys VALUES
(30,  (SELECT id FROM big_five_codes WHERE small_code='C6'), true,  '나는 깊이 생각하지 않고 덤비는 편이다.'),
(60,  (SELECT id FROM big_five_codes WHERE small_code='C6'), true,  '나는 성급하게 결정을 내리는 편이다.'),
(90,  (SELECT id FROM big_five_codes WHERE small_code='C6'), true,  '나는 일을 성급하게 처리하는 편이다.'),
(120, (SELECT id FROM big_five_codes WHERE small_code='C6'), true,  '나는 깊이 생각하지 않고 행동하는 편이다.');

-- =========================
-- User 1: 120개 전부 응답
-- 값 패턴(보기 자연스럽게 분포): 4,3,5,2,1 반복
-- =========================
INSERT INTO survey_responses (survey_id, user_id, value, started_at) VALUES
(1, 1, 4, NOW()), (2, 1, 3, NOW()), (3, 1, 5, NOW()), (4, 1, 2, NOW()), (5, 1, 1, NOW()),
(6, 1, 4, NOW()), (7, 1, 3, NOW()), (8, 1, 5, NOW()), (9, 1, 2, NOW()), (10, 1, 1, NOW()),
(11, 1, 4, NOW()), (12, 1, 3, NOW()), (13, 1, 5, NOW()), (14, 1, 2, NOW()), (15, 1, 1, NOW()),
(16, 1, 4, NOW()), (17, 1, 3, NOW()), (18, 1, 5, NOW()), (19, 1, 2, NOW()), (20, 1, 1, NOW()),
(21, 1, 4, NOW()), (22, 1, 3, NOW()), (23, 1, 5, NOW()), (24, 1, 2, NOW()), (25, 1, 1, NOW()),
(26, 1, 4, NOW()), (27, 1, 3, NOW()), (28, 1, 5, NOW()), (29, 1, 2, NOW()), (30, 1, 1, NOW()),
(31, 1, 4, NOW()), (32, 1, 3, NOW()), (33, 1, 5, NOW()), (34, 1, 2, NOW()), (35, 1, 1, NOW()),
(36, 1, 4, NOW()), (37, 1, 3, NOW()), (38, 1, 5, NOW()), (39, 1, 2, NOW()), (40, 1, 1, NOW()),
(41, 1, 4, NOW()), (42, 1, 3, NOW()), (43, 1, 5, NOW()), (44, 1, 2, NOW()), (45, 1, 1, NOW()),
(46, 1, 4, NOW()), (47, 1, 3, NOW()), (48, 1, 5, NOW()), (49, 1, 2, NOW()), (50, 1, 1, NOW()),
(51, 1, 4, NOW()), (52, 1, 3, NOW()), (53, 1, 5, NOW()), (54, 1, 2, NOW()), (55, 1, 1, NOW()),
(56, 1, 4, NOW()), (57, 1, 3, NOW()), (58, 1, 5, NOW()), (59, 1, 2, NOW()), (60, 1, 1, NOW()),
(61, 1, 4, NOW()), (62, 1, 3, NOW()), (63, 1, 5, NOW()), (64, 1, 2, NOW()), (65, 1, 1, NOW()),
(66, 1, 4, NOW()), (67, 1, 3, NOW()), (68, 1, 5, NOW()), (69, 1, 2, NOW()), (70, 1, 1, NOW()),
(71, 1, 4, NOW()), (72, 1, 3, NOW()), (73, 1, 5, NOW()), (74, 1, 2, NOW()), (75, 1, 1, NOW()),
(76, 1, 4, NOW()), (77, 1, 3, NOW()), (78, 1, 5, NOW()), (79, 1, 2, NOW()), (80, 1, 1, NOW()),
(81, 1, 4, NOW()), (82, 1, 3, NOW()), (83, 1, 5, NOW()), (84, 1, 2, NOW()), (85, 1, 1, NOW()),
(86, 1, 4, NOW()), (87, 1, 3, NOW()), (88, 1, 5, NOW()), (89, 1, 2, NOW()), (90, 1, 1, NOW()),
(91, 1, 4, NOW()), (92, 1, 3, NOW()), (93, 1, 5, NOW()), (94, 1, 2, NOW()), (95, 1, 1, NOW()),
(96, 1, 4, NOW()), (97, 1, 3, NOW()), (98, 1, 5, NOW()), (99, 1, 2, NOW()), (100, 1, 1, NOW()),
(101, 1, 4, NOW()), (102, 1, 3, NOW()), (103, 1, 5, NOW()), (104, 1, 2, NOW()), (105, 1, 1, NOW()),
(106, 1, 4, NOW()), (107, 1, 3, NOW()), (108, 1, 5, NOW()), (109, 1, 2, NOW()), (110, 1, 1, NOW()),
(111, 1, 4, NOW()), (112, 1, 3, NOW()), (113, 1, 5, NOW()), (114, 1, 2, NOW()), (115, 1, 1, NOW()),
(116, 1, 4, NOW()), (117, 1, 3, NOW()), (118, 1, 5, NOW()), (119, 1, 2, NOW()), (120, 1, 1, NOW());

-- =========================
-- User 2: 120개 전부 응답
-- 값 패턴(다른 분포): 2,5,3,4,1 반복
-- =========================
INSERT INTO survey_responses (survey_id, user_id, value, started_at) VALUES
(1, 2, 2, NOW()), (2, 2, 5, NOW()), (3, 2, 3, NOW()), (4, 2, 4, NOW()), (5, 2, 1, NOW()),
(6, 2, 2, NOW()), (7, 2, 5, NOW()), (8, 2, 3, NOW()), (9, 2, 4, NOW()), (10, 2, 1, NOW()),
(11, 2, 2, NOW()), (12, 2, 5, NOW()), (13, 2, 3, NOW()), (14, 2, 4, NOW()), (15, 2, 1, NOW()),
(16, 2, 2, NOW()), (17, 2, 5, NOW()), (18, 2, 3, NOW()), (19, 2, 4, NOW()), (20, 2, 1, NOW()),
(21, 2, 2, NOW()), (22, 2, 5, NOW()), (23, 2, 3, NOW()), (24, 2, 4, NOW()), (25, 2, 1, NOW()),
(26, 2, 2, NOW()), (27, 2, 5, NOW()), (28, 2, 3, NOW()), (29, 2, 4, NOW()), (30, 2, 1, NOW()),
(31, 2, 2, NOW()), (32, 2, 5, NOW()), (33, 2, 3, NOW()), (34, 2, 4, NOW()), (35, 2, 1, NOW()),
(36, 2, 2, NOW()), (37, 2, 5, NOW()), (38, 2, 3, NOW()), (39, 2, 4, NOW()), (40, 2, 1, NOW()),
(41, 2, 2, NOW()), (42, 2, 5, NOW()), (43, 2, 3, NOW()), (44, 2, 4, NOW()), (45, 2, 1, NOW()),
(46, 2, 2, NOW()), (47, 2, 5, NOW()), (48, 2, 3, NOW()), (49, 2, 4, NOW()), (50, 2, 1, NOW()),
(51, 2, 2, NOW()), (52, 2, 5, NOW()), (53, 2, 3, NOW()), (54, 2, 4, NOW()), (55, 2, 1, NOW()),
(56, 2, 2, NOW()), (57, 2, 5, NOW()), (58, 2, 3, NOW()), (59, 2, 4, NOW()), (60, 2, 1, NOW()),
(61, 2, 2, NOW()), (62, 2, 5, NOW()), (63, 2, 3, NOW()), (64, 2, 4, NOW()), (65, 2, 1, NOW()),
(66, 2, 2, NOW()), (67, 2, 5, NOW()), (68, 2, 3, NOW()), (69, 2, 4, NOW()), (70, 2, 1, NOW()),
(71, 2, 2, NOW()), (72, 2, 5, NOW()), (73, 2, 3, NOW()), (74, 2, 4, NOW()), (75, 2, 1, NOW()),
(76, 2, 2, NOW()), (77, 2, 5, NOW()), (78, 2, 3, NOW()), (79, 2, 4, NOW()), (80, 2, 1, NOW()),
(81, 2, 2, NOW()), (82, 2, 5, NOW()), (83, 2, 3, NOW()), (84, 2, 4, NOW()), (85, 2, 1, NOW()),
(86, 2, 2, NOW()), (87, 2, 5, NOW()), (88, 2, 3, NOW()), (89, 2, 4, NOW()), (90, 2, 1, NOW()),
(91, 2, 2, NOW()), (92, 2, 5, NOW()), (93, 2, 3, NOW()), (94, 2, 4, NOW()), (95, 2, 1, NOW()),
(96, 2, 2, NOW()), (97, 2, 5, NOW()), (98, 2, 3, NOW()), (99, 2, 4, NOW()), (100, 2, 1, NOW()),
(101, 2, 2, NOW()), (102, 2, 5, NOW()), (103, 2, 3, NOW()), (104, 2, 4, NOW()), (105, 2, 1, NOW()),
(106, 2, 2, NOW()), (107, 2, 5, NOW()), (108, 2, 3, NOW()), (109, 2, 4, NOW()), (110, 2, 1, NOW()),
(111, 2, 2, NOW()), (112, 2, 5, NOW()), (113, 2, 3, NOW()), (114, 2, 4, NOW()), (115, 2, 1, NOW()),
(116, 2, 2, NOW()), (117, 2, 5, NOW()), (118, 2, 3, NOW()), (119, 2, 4, NOW()), (120, 2, 1, NOW());

-- =========================
-- User 3: 일부만 응답 (1~20번만)
-- =========================
INSERT INTO survey_responses (survey_id, user_id, value, started_at) VALUES
(1, 3, 3, NOW()), (2, 3, 2, NOW()), (3, 3, 4, NOW()), (4, 3, 5, NOW()), (5, 3, 2, NOW()),
(6, 3, 4, NOW()), (7, 3, 1, NOW()), (8, 3, 3, NOW()), (9, 3, 5, NOW()), (10, 3, 4, NOW()),
(11, 3, 2, NOW()), (12, 3, 5, NOW()), (13, 3, 3, NOW()), (14, 3, 2, NOW()), (15, 3, 4, NOW()),
(16, 3, 1, NOW()), (17, 3, 3, NOW()), (18, 3, 5, NOW()), (19, 3, 2, NOW()), (20, 3, 4, NOW());