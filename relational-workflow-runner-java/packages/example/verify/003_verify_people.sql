SELECT 'unexpected row count' AS error_message
FROM dual
WHERE (SELECT COUNT(*) FROM EXAMPLE_PEOPLE) <> 3
UNION ALL
SELECT 'missing required sample person' AS error_message
FROM dual
WHERE NOT EXISTS (
  SELECT 1
  FROM EXAMPLE_PEOPLE
  WHERE PERSON_ID = 1
    AND FULL_NAME = 'Ada Lovelace'
);
