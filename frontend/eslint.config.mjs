import { FlatCompat } from "@eslint/eslintrc"
import { dirname } from "path"
import { fileURLToPath } from "url"
import stylistic from "@stylistic/eslint-plugin"
import unusedImports from "eslint-plugin-unused-imports"

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

const compat = new FlatCompat({
  baseDirectory: __dirname
})

const config = [
  // Next.js 권장 규칙 불러오기
  ...compat.extends("next/core-web-vitals", "next/typescript", "prettier"),

  // 무시할 경로
  {
    ignores: [
      "node_modules/**",
      ".next/**",
      "out/**",
      "build/**",
      "next-env.d.ts",
      "api/gen/**" // 자동 생성된 API 클라이언트 코드
    ]
  },

  // 플러그인 적용
  {
    files: ["**/*.{js,jsx,ts,tsx}"],
    plugins: {
      "unused-imports": unusedImports
    },
    rules: {

      // unused-imports 규칙
      "unused-imports/no-unused-imports": "error",
      "unused-imports/no-unused-vars": [
        "warn",
        {
          vars: "all",
          varsIgnorePattern: "^_",
          args: "after-used",
          argsIgnorePattern: "^_"
        }
      ],

      "react/react-in-jsx-scope": "off",
      "no-console": ["warn", { allow: ["warn", "error"] }],
      "jsx-a11y/alt-text": "warn",
      "jsx-a11y/anchor-is-valid": "warn",

      // 중복 방지: 기본 no-unused-vars 비활성화
      "@typescript-eslint/no-unused-vars": "off",

      // any 타입 사용 경고
      "@typescript-eslint/no-explicit-any": "warn",
      "@typescript-eslint/no-empty-object-type": "warn"
    }
  }
]

export default config
