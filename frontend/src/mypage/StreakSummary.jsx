import React from "react";
import { StatsBox, Label, Value } from "./MyPageStyles";

const StreakSummary = ({ streak }) => (
  <StatsBox>
    <Label>연속 독서 기록</Label>
    <Value>{streak}일</Value>
  </StatsBox>
);

export default StreakSummary;
