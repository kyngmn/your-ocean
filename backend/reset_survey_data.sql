-- Survey 데이터 완전 초기화

-- 1. 기존 데이터 삭제
DELETE FROM survey_responses;
DELETE FROM surveys;
DELETE FROM big_five_codes;

-- 2. 시퀀스 초기화
ALTER SEQUENCE surveys_id_seq RESTART WITH 1;
ALTER SEQUENCE big_five_codes_id_seq RESTART WITH 1;

-- 3. 확인
SELECT 'surveys_id_seq current value:', last_value FROM surveys_id_seq;
SELECT 'big_five_codes_id_seq current value:', last_value FROM big_five_codes_id_seq;