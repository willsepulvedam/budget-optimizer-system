import ApexCharts from 'apexcharts';

document.addEventListener("DOMContentLoaded", function () {
  const timePeriodSelect = document.getElementById("timePeriod");
  const categorySelect = document.getElementById("categoria");
  const resultDiv = document.getElementById("result");

  timePeriodSelect.addEventListener("change", () => {
    console.log("Tiempo de periodo seleccionado:", timePeriodSelect.value);
  });

  categorySelect.addEventListener("change", () => {
    console.log("Categoría seleccionada:", categorySelect.value);
  });

  // Gráfica 1
  const chartOptions1 = {
    series: [
      {
        name: "Sistema de ventas",
        data: [12000, 15000, 11000, 18000, 22000, 19000, 25000, 23000, 48000, 30000, 14000],
      },
    ],
    chart: { type: "area", height: 350, zoom: { enabled: false } },
    dataLabels: { enabled: false },
    stroke: { curve: "straight" },
    title: { text: "Análisis de Ventas", align: "left" },
    subtitle: { text: "Evolución de precios", align: "left" },
    labels: [
      "2025-01-10", "2025-02-20", "2025-03-07", "2025-04-14", "2025-05-31",
      "2025-06-11", "2025-07-18", "2025-10-31", "2025-12-07", "2025-12-24", "2025-12-31",
    ],
    yaxis: { opposite: true },
    legend: { horizontalAlign: "left" },
  };

  new ApexCharts(document.querySelector("#chart"), chartOptions1).render();

  // Gráfica 2
  const chartOptions2 = {
    series: [{ name: "Inflation", data: [2.3, 3.1, 4.0, 10.1, 4.0, 3.6, 3.2, 2.3, 1.4, 0.8, 0.5, 0.2] }],
    chart: { height: 350, type: "bar" },
    plotOptions: {
      bar: { borderRadius: 10, dataLabels: { position: "top" } },
    },
    dataLabels: {
      enabled: true,
      formatter: val => val + "%",
      offsetY: -20,
      style: { fontSize: "12px", colors: ["#304758"] },
    },
    xaxis: {
      categories: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
      position: "top",
      axisBorder: { show: false },
      axisTicks: { show: false },
    },
    yaxis: { labels: { show: false } },
    title: { text: "Ventas en Colombia, 2050", floating: true, offsetY: 330, align: "center" },
  };

  new ApexCharts(document.querySelector("#chart1"), chartOptions2).render();
});
