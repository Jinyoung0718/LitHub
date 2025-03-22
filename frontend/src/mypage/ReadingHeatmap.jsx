import React from "react";
import ReactTooltip from "react-tooltip";
import CalendarHeatmap from "react-calendar-heatmap";
import "react-calendar-heatmap/dist/styles.css";
import "react-tooltip/dist/react-tooltip.css";

const ReadingHeatmap = ({ readingRecords = [] }) => {
  const year = new Date().getFullYear();
  const startDate = new Date(`${year}-01-01`);
  const endDate = new Date(`${year}-12-31`);

  const values = readingRecords.map((record) => ({
    date: record.date,
    count: record.colorLevel,
  }));

  return (
    <div style={{ marginTop: "40px" }}>
      <h3>연간 독서 히트맵</h3>
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
            "data-tip": `${value.date} - 레벨 ${value.count}`,
          };
        }}
      />
      <ReactTooltip />
    </div>
  );
};

export default ReadingHeatmap;
