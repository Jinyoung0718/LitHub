import React from "react";
import CalendarHeatmap from "react-calendar-heatmap";
import { Tooltip as ReactTooltip } from "react-tooltip";
import {
  HeatmapContainer,
  LegendContainer,
  LegendBoxWrapper,
  ColorBlock,
} from "./ReadingHeatmapStyles";

const colorLevels = {
  1: "#a5d6a7",
  2: "#66bb6a",
  3: "#388e3c",
  4: "#1b5e20",
  5: "#0d3f17",
};

const ReadingHeatmap = ({ readingRecords = [], selectedYear }) => {
  const startDate = new Date(`${selectedYear}-01-01`);
  const endDate = new Date(`${selectedYear}-12-31`);

  const values = readingRecords.map((record) => ({
    date: record.date,
    count: record.colorLevel,
  }));

  return (
    <HeatmapContainer>
      <CalendarHeatmap
        startDate={startDate}
        endDate={endDate}
        values={values}
        showWeekdayLabels={false}
        classForValue={(value) => {
          if (!value || value.count === 0) return "color-empty";
          return `color-github-${value.count}`;
        }}
        tooltipDataAttrs={(value) =>
          value?.date
            ? {
                "data-tooltip-id": "reading-tooltip",
                "data-tooltip-content": `${value.date} - 레벨 ${value.count}`,
              }
            : null
        }
      />
      <ReactTooltip id="reading-tooltip" />

      <LegendContainer>
        {[1, 2, 3, 4, 5].map((level) => (
          <LegendBoxWrapper key={level}>
            <ColorBlock color={colorLevels[level]} />
            <span>
              {level === 1
                ? "10분 이하"
                : level === 2
                ? "~30분"
                : level === 3
                ? "~1시간"
                : level === 4
                ? "~2시간"
                : "2시간 이상"}
            </span>
          </LegendBoxWrapper>
        ))}
      </LegendContainer>
    </HeatmapContainer>
  );
};

export default ReadingHeatmap;
