import { createGlobalStyle } from "styled-components";

const GlobalStyle = createGlobalStyle`
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  body {
    font-family: 'Roboto', sans-serif;
    background-color: #fffdf7;
  }

  #root {
    margin: 0 auto;
    text-align: center;
  }

  .logo {
    height: 6em;
    padding: 1.5em;
    transition: filter 300ms;
  }

  .logo:hover {
    filter: drop-shadow(0 0 2em #646cffaa);
  }

  .logo.react:hover {
    filter: drop-shadow(0 0 2em #61dafbaa);
  }

  @keyframes logo-spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }

  @media (prefers-reduced-motion: no-preference) {
    a:nth-of-type(2) .logo {
      animation: logo-spin infinite 20s linear;
    }
  }

  .card {
    padding: 2em;
  }

  .read-the-docs {
    color: #888;
  }

  .react-calendar-heatmap text {
    font-size: 6px;
  }

  .react-calendar-heatmap .color-empty {
    fill: #e0e0e0;
  }

  .react-calendar-heatmap .color-github-1 {
    fill: #a5d6a7;
  }

  .react-calendar-heatmap .color-github-2 {
    fill: #66bb6a;
  }

  .react-calendar-heatmap .color-github-3 {
    fill: #388e3c;
  }

  .react-calendar-heatmap .color-github-4 {
    fill: #1b5e20;
  }

  .react-calendar-heatmap .color-github-5 {
    fill: #0d3f17;
  }
`;
export default GlobalStyle;
