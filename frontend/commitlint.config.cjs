module.exports = {
  extends: ["@commitlint/config-conventional"],
  rules: {
    // 한국어/혼합 메시지 허용
    "subject-case": [0]
    // 팀 스코프 미리 고정하면 아래 scope-enum 사용
    // 'scope-enum': [2, 'always', ['app','api','ui','auth','store','config','deps']],
  }
}
