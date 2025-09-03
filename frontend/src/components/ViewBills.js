import React, { useEffect, useState } from "react";
import axios from "axios";

const ViewBills = () => {
    const userId =  JSON.parse(localStorage.getItem("user"))?.id || 0
    const [bills, setBills] = useState([]);

    useEffect(()=>{
        axios.get(`http://localhost:9090/api/bills/${userId}`)
            .then(res => setBills(res.data))
            .catch(err => console.error("error fetching bills:", err))
    }, [])

    const handleDownload = async (billTitle, billId) => {
        try {
            const response = await fetch(`http://localhost:9090/api/bills/${billId}/download`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `${billTitle}_${billId}.pdf`); // Desired filename
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Error downloading PDF:', error);
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
                        <th>Download Bill</th>
                    </tr>
                    </thead>
                    <tbody>
                    {bills.map((bill) => (
                        <tr key={bill.id}>
                            <td>{bill.title}</td>
                            <td>{bill.category}</td>
                            <td>₹{bill.amount}</td>
                            <td>{bill.dueDate}</td>
                            <td>
                                <button
                                    onClick = {() => handleDownload(bill.title, bill.id)}
                                    className="btn btn-sm"
                                    style={{ backgroundColor: "#ff7f50", color: "white", border: "none" }}
                                >
                                    Download
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default ViewBills;
