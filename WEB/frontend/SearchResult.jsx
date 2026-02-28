import { useState, useEffect, use } from "react"
import { useParams } from "react-router-dom"

export default function SearchResult() {
    const { searchedWord } = useParams();
    const [searchResults, setSearchResults] = useState([]);

    useEffect(() => {
        function fetchData() {
            fetch('http://localhost:3333/api/TODO' + searchedWord)
                .then(response => response.json())
                .then(data => {
                    setSearchResults(data);
                })
                .catch(error => console.warn('Error fetching:', error));
        }

        fetchData();
    }, [searchedWord, setSearchResults]);

    return (
        
    )
}