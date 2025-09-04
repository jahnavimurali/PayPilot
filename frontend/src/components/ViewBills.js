import React, { useEffect, useState } from "react";
import axios from "axios";

const ViewBills = () => {
    const [userId, setUserId] = useState(null);
    const [bills, setBills] = useState([]);

    useEffect(() => {
        const user = JSON.parse(localStorage.getItem("user"));
        if (user?.id) {
            setUserId(user.id);
        }
    }, []);

    useEffect(() => {
        if (userId) {
            fetchBills(userId);
        }
    }, [userId]);

    const fetchBills = async (userId) => {
        try {
            const res = await axios.get(`http://localhost:9090/api/bills/${userId}`);
            // 🚫 filter out bills with CASH method
            setBills(res.data.filter((b) => b.paymentMethod !== "CASH"));
        } catch (err) {
            console.error("Error fetching bills:", err);
        }
    };

    const handleDownload = async (billTitle, billId) => {
        try {
            const response = await fetch(
                `http://localhost:9090/api/bills/${billId}/download`
            );
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", `${billTitle}_${billId}.pdf`);
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error("Error downloading PDF:", error);
        }
    };

    return (
        <div>
            <h3>All Bills</h3>
            {bills.length === 0 ? (
                <p>No bills found.</p>
            ) : (
                <table className="table table-striped mt-3">
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Amount</th>
                        <th>Due Date</th>
                        <th>Payment</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {bills.map((bill) => {
                        const autoPayFlag =
                            bill.autoPayEnabled ?? bill.isAutoPayEnabled;

                        return (
                            <tr key={bill.id}>
                                <td>{bill.title}</td>
                                <td>{bill.category}</td>
                                <td>₹{bill.amount}</td>
                                <td>{bill.dueDate}</td>
                                <td>
                                    {autoPayFlag ? (
                                        <span className="badge bg-success">⚡ Auto-Pay</span>
                                    ) : (
                                        <span className="badge bg-secondary">
                        🖐️ Manual ({bill.paymentMethod})
                      </span>
                                    )}
                                </td>
                                <td>
                                    <button
                                        onClick={() => handleDownload(bill.title, bill.id)}
                                        className="btn btn-sm btn-success me-2"
                                    >
                                        Download
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default ViewBills;
