import React from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const MonthlyStatsChart = ({ monthlyStats }) => {
  const data = monthlyStats.map((stat) => ({
    name: `${stat.month}월`,
    time: stat.totalReadingTime,
  }));

  const isEmpty = data.length === 0;

  return (
    <div style={{ marginTop: "40px" }}>
      <h3>월별 총 독서 시간</h3>
      {isEmpty ? (
        <p style={{ color: "#777", fontStyle: "italic" }}>
          아직 독서 기록이 없습니다.
        </p>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="time" fill="#ffa726" />
          </BarChart>
        </ResponsiveContainer>
      )}
    </div>
  );
};

export default MonthlyStatsChart;
