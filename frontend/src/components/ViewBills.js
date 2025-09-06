import React, { useEffect, useState, useMemo } from "react";
import axios from "axios";

const ViewBills = () => {
    const [userId, setUserId] = useState(null);
    const [allBills, setAllBills] = useState([]);
    // const [schedPays, setSchedPays] = useState([]); // Scheduled payments for status
    const [filter, setFilter] = useState("ALL");    // ALL | UNPAID | PAID
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const user = JSON.parse(localStorage.getItem("user"));
        if (user?.id) setUserId(user.id);
    }, []);

    useEffect(() => {
        if (!userId) return;
        (async () => {
            try {
                setLoading(true);
                const [billsRes] = await Promise.all([
                    axios.get(`http://localhost:9090/api/bills/${userId}`),
                    // axios.get(`http://localhost:9090/api/scheduled-payments/user/${userId}`),
                ]);
                // keep CASH bills out if you don’t want them
                const bills = (billsRes.data || []).filter(b => b.paymentMethod !== "CASH");
                setAllBills(bills);
                // setSchedPays(schedRes.data || []);
            } catch (e) {
                console.error("Fetch error:", e);
            } finally {
                setLoading(false);
            }
        })();
    }, [userId]);

    // Build a quick lookup: billId -> isPaid (true if any scheduled payment for that bill is marked paid)
    // const isPaidMap = useMemo(() => {
    //     const map = new Map();
    //     for (const sp of schedPays) {
    //         if (sp?.billId == null) continue;
    //         const paid = Boolean(sp?.isPaid);
    //         // if any scheduled payment for this bill is paid, treat the bill as paid
    //         map.set(sp.billId, (map.get(sp.billId) || false) || paid);
    //     }
    //     return map;
    // }, [schedPays]);

    const filteredBills = useMemo(() => {
        if (filter === "ALL") return allBills;
        if (filter === "PAID") return allBills.filter(b => Boolean(b.isPaid));
        // UNPAID
        return allBills.filter(b => !Boolean(b.isPaid));
    }, [filter, allBills]);

    const handleDownload = async (billTitle, billId) => {
        try {
            const response = await fetch(`http://localhost:9090/api/bills/${billId}/download`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = `${billTitle}_${billId}.pdf`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            console.error("Error downloading PDF:", err);
        }
    };

    return (
        <div>
            <h3>Bills</h3>

            <div className="mb-3">
                <label className="form-label me-2">Filter:</label>
                <select
                    className="form-select w-auto d-inline-block"
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                >
                    <option value="ALL">All Bills</option>
                    <option value="UNPAID">Unpaid Bills</option>
                    <option value="PAID">Paid Bills</option>
                </select>
            </div>

            {loading ? (
                <p>Loading...</p>
            ) : filteredBills.length === 0 ? (
                <p>No bills found.</p>
            ) : (
                <table className="table table-striped mt-3">
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Category</th>
                        <th>Amount</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Payment</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {filteredBills.map((bill) => {
                        const autoPayFlag = bill.autoPayEnabled ?? bill.isAutoPayEnabled;
                        const paid = Boolean(bill.isPaid);

                        return (
                            <tr key={bill.id}>
                                <td>{bill.title}</td>
                                <td>{bill.category}</td>
                                <td>₹{bill.amount}</td>
                                <td>{bill.dueDate}</td>
                                <td>
                                    {paid ? (
                                        <span className="badge bg-success">✅ Paid</span>
                                    ) : (
                                        <span className="badge bg-danger">❌ Unpaid</span>
                                    )}
                                </td>
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