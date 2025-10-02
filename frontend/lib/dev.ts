// 개발 모드일 때만 로그를 출력하는 유틸리티 함수
export const dev = {
  log: (...args: any[]) => {
    if (process.env.NODE_ENV === "development" && args) {
      console.log(`[DEV] `, ...args)
    }
  },
  warn: (...args: any[]) => {
    if (process.env.NODE_ENV === "development" && args) {
      console.warn(`[DEV] `, ...args)
    }
  },
  error: (...args: any[]) => {
    if (process.env.NODE_ENV === "development" && args) {
      console.error(`[DEV] `, ...args)
    }
  }
}
