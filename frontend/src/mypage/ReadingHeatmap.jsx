import React from "react";
import CalendarHeatmap from "react-calendar-heatmap";
import { Tooltip as ReactTooltip } from "react-tooltip";
import "react-calendar-heatmap/dist/styles.css";
import "react-tooltip/dist/react-tooltip.css";

const ReadingHeatmap = ({ readingRecords = [], selectedYear }) => {
  const startDate = new Date(`${selectedYear}-01-01`);
  const endDate = new Date(`${selectedYear}-12-31`);

  const values = readingRecords.map((record) => ({
    date: record.date,
    count: record.colorLevel,
  }));

  return (
    <div style={{ marginTop: "40px" }}>
      <h3>{selectedYear}년 독서 히트맵</h3>
      <CalendarHeatmap
        startDate={startDate}
        endDate={endDate}
        values={values}
        showWeekdayLabels={false}
        classForValue={(value) => {
          if (!value || value.count === 0) return "color-empty";
          return `color-github-${value.count}`;
        }}
        tooltipDataAttrs={(value) => {
          if (!value || !value.date) return null;
          return {
            "data-tooltip-id": "reading-tooltip",
            "data-tooltip-content": `${value.date} - 레벨 ${value.count}`,
          };
        }}
      />
      <ReactTooltip id="reading-tooltip" />
    </div>
  );
};

export default ReadingHeatmap;
