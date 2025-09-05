import React, { useState, useEffect } from "react";
import axios from "axios";

const PaymentManager = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    const [payments, setPayments] = useState([]);
    const [allBills, setAllBills] = useState([]);

    useEffect(() => {
        fetchPayments();
        fetchBills();
        // eslint-disable-next-line
    }, []);

    const fetchPayments = async () => {
        try {
            const res = await axios.get(
                `http://localhost:9090/api/payments/user/${user.id}`
            );
            setPayments(res.data || []);
        } catch (err) {
            console.error("Error fetching payments:", err);
        }
    };

    const fetchBills = async () => {
        try {
            const billsRes = await axios.get(
                `http://localhost:9090/api/bills/${user.id}`
            );
            setAllBills(billsRes.data || []);
        } catch (err) {
            console.error("Error fetching bills:", err);
        }
    };

    return (
        <div className="container mt-4">
            <h3>💳 All Payments</h3>
            {payments.length === 0 ? (
                <p>No payments found.</p>
            ) : (
                <table className="table table-bordered mt-3">
                    <thead>
                    <tr>
                        <th>Payment ID</th>
                        <th>Bill</th>
                        <th>Amount</th>
                        <th>Method</th>
                        <th>Scheduled Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    {payments.map((p) => {
                        const bill = allBills.find((b) => b.id === p.billId);
                        return (
                            <tr key={p.id}>
                                <td>{p.id}</td>
                                <td>{bill ? bill.title : `Bill #${p.billId}`}</td>
                                <td>{p.amount}</td>
                                <td>{p.method}</td>
                                <td>{p.scheduledDate}</td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default PaymentManager;
