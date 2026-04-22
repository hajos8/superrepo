import { Card, CardContent, CardHeader, CardMedia, Container, Stack, Typography } from '@mui/material';
import { useState, useEffect } from 'react';

export default function Home() {
    const [TODO, setTODO] = useState([]);

    useEffect(() => {
        function fetchData() {
            fetch('http://localhost:3333/TODO')
                .then(response => response.json())
                .then(data => {
                    setTODO(data);
                })
                .catch(error => console.warn('Error fetching:', error));
        }

        fetchData();
    }, [setTODO]);

    return (
        <>
            <Container>
                <Stack spacing={2}>
                    {TODO.map((item, index) => {
                        return (
                            <Card key={index}>
                                <CardHeader title={item.title} subheader={item.description} /> {/* card title */}
                                <CardMedia>
                                    {/* image */}
                                </CardMedia>
                                <CardContent>
                                    <Typography variant="body2" color="blue"> {/* h1, h2... body1, body2 */}
                                        {/* description */}
                                    </Typography>

                                    {/* content */}
                                    {JSON.stringify(item)} {/* itt láthatód az item objektumot */}
                                </CardContent>
                            </Card>
                        );
                    })}
                </Stack>
            </Container>
        </>
    )
}