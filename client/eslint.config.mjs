import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';
import pluginReact from 'eslint-plugin-react';
import pluginReactHooks from 'eslint-plugin-react-hooks';

export default tseslint.config(
  eslint.configs.recommended, // ESLint 기본 권장 규칙
  ...tseslint.configs.recommended, // TypeScript ESLint 권장 규칙 (배열이므로 펼쳐서 사용)
  ...tseslint.configs.stylistic, // TypeScript 스타일 관련 규칙 (배열이므로 펼쳐서 사용)
  ...tseslint.configs.strict, // TypeScript 엄격한 규칙 (배열이므로 펼쳐서 사용)
  
  {
    files: ['**/*.{ts,tsx}'], // .ts, .tsx 파일에만 적용
    plugins: {
      react: pluginReact,
      'react-hooks': pluginReactHooks,
    },
    rules: {
      ...pluginReact.configs.recommended.rules,
      ...pluginReactHooks.configs.recommended.rules,
    },
    languageOptions: { // parserOptions 대신 사용
      parser: '@typescript-eslint/parser',
      ecmaFeatures: {
        jsx: true,
      },
    },
    settings: { // settings 객체는 그대로 사용 가능
      react: {
        version: 'detect',
      },
    },
  }
);
