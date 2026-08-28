# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/2 (100.0%)
- **Function parity:** 22/22 matched (target 27) — 100.0%
- **Class/type parity:** 0/0 matched (target 2) — N/A
- **Combined symbol parity:** 22/22 matched (target 29) — 100.0%
- **Average inline-code cosine:** 0.38 (function body across 2 matched files)
- **Average documentation cosine:** 0.94 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 1 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. lib

- **Target:** `winsplit.Lib [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1000010.0
- **Functions:** 0/0 matched (target 1)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 2. vc_2008

- **Target:** `winsplit.Vc2008`
- **Similarity:** 0.75
- **Dependents:** 0
- **Priority Score:** 2202.5
- **Functions:** 22/22 matched (target 26)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 18/18 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

