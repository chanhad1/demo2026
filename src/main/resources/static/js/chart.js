document.addEventListener("DOMContentLoaded", function () {
    const canvas = document.getElementById("myChart");
    if (!canvas) return;

    const revenue = parseFloat(canvas.getAttribute("data-revenue")) || 0;
    const orders = parseInt(canvas.getAttribute("data-orders")) || 0;

    new Chart(canvas, {
        type: 'bar',
        data: {
            labels: ['Doanh thu (VNĐ)', 'Số đơn hàng'],
            datasets: [{
                label: 'Thống kê',
                data: [revenue, orders],
                backgroundColor: ['#4CAF50', '#2196F3'],
                borderColor: ['#388E3C', '#1976D2'],
                borderWidth: 2,
                borderRadius: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: true,
                    text: 'BÁO CÁO DOANH THU & ĐƠN HÀNG',
                    font: {
                        size: 16,
                        weight: 'bold'
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            if (this.chart.data.datasets[0].label === 'Doanh thu (VNĐ)') {
                                return value.toLocaleString('vi-VN') + ' đ';
                            }
                            return value;
                        }
                    }
                }
            }
        }
    });
});